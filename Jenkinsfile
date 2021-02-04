#!groovy
@Library(['github.com/cloudogu/ces-build-lib@1.44.3', 'github.com/cloudogu/dogu-build-lib@v1.2.0'])
import com.cloudogu.ces.cesbuildlib.*
import com.cloudogu.ces.dogubuildlib.*

node() { // No specific label

    project = 'github.com/cloudogu/smeagol'
    projectName = 'smeagol'
    branch = "${env.BRANCH_NAME}"

    properties([
            // Keep only the last 10 build to preserve space
            buildDiscarder(logRotator(numToKeepStr: '10')),
            // Don't run concurrent builds for a branch, because they use the same workspace directory
            disableConcurrentBuilds()
    ])

    String defaultEmailRecipients = env.EMAIL_RECIPIENTS
    Git git = new Git(this, "cesmarvin")
    EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "smeagol-gcloud-ces-operations-internal")

    catchError {

        def mvnDockerName = '3.6-openjdk-8'
        Maven mvn = new MavenInDocker(this, mvnDockerName)

        stage('Checkout') {
            checkout scm
            git.clean("")
        }

        stage('Lint') {
            lintDockerfile()
        }

        stage('Build Smeagol') {
            setupMaven(mvn)
            mvn 'clean install -DskipTests'
            archive '**/target/*.*ar,**/target/*.zip'
        }

        stage('Unit Test') {
            mvn 'test'
        }

        try {
            stage('Provision') {
                ecoSystem.provision("/dogu")
            }

            stage('Setup') {
                ecoSystem.loginBackend('cesmarvin-setup')
                ecoSystem.setup([additionalDependencies: ['official/scm']])
            }

            stage('Wait for dependencies') {
                timeout(15) {
                    ecoSystem.waitForDogu("scm")
                }
            }

            stage('Build Dogu') {
               ecoSystem.build("/dogu")
            }

            stage('SonarQube') {
                def scannerHome = tool name: 'sonar-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                withSonarQubeEnv {


                    sh "git config 'remote.origin.fetch' '+refs/heads/*:refs/remotes/origin/*'"
                    gitWithCredentials("fetch --all")

                    if (branch == "master") {
                        echo "This branch has been detected as the master branch."
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} -Dsonar.projectName=${projectName}"
                    } else if (branch == "develop") {
                        echo "This branch has been detected as the develop branch."
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} -Dsonar.projectName=${projectName} -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.branch.target=master  "
                    } else if (env.CHANGE_TARGET) {
                        echo "This branch has been detected as a pull request."
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} -Dsonar.projectName=${projectName} -Dsonar.branch.name=${env.CHANGE_BRANCH}-PR${env.CHANGE_ID} -Dsonar.branch.target=${env.CHANGE_TARGET} "
                    } else if (branch.startsWith("feature/")) {
                        echo "This branch has been detected as a feature branch."
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} -Dsonar.projectName=${projectName} -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.branch.target=develop"
                    }
                }
                timeout(time: 2, unit: 'MINUTES') { // Needed when there is no webhook for example
                    def qGate = waitForQualityGate()
                    if (qGate.status != 'OK') {
                        unstable("Pipeline unstable due to SonarQube quality gate failure")
                    }
                }
            }
        } finally {
          stage('Clean') {
            ecoSystem.destroy()
          }
       }
    }

    // Archive Unit and integration test results, if any
    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml,**/target/jest-reports/TEST-*.xml'

    mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
}

def setupMaven(mvn) {
    if ("master".equals(env.BRANCH_NAME)) {
        mvn.additionalArgs = "-DperformRelease"
        currentBuild.description = mvn.getVersion()
    }
}

void gitWithCredentials(String command){
    withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
        sh (
                script: "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" " + command,
                returnStdout: true
        )
    }
}
