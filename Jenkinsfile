#!groovy
@Library(['github.com/cloudogu/ces-build-lib@2.2.1', 'github.com/cloudogu/dogu-build-lib@v2.3.1'])
import com.cloudogu.ces.cesbuildlib.*
import com.cloudogu.ces.dogubuildlib.*

String doguName = 'smeagol'
String branch = "${env.BRANCH_NAME}"
String defaultEmailRecipients = env.EMAIL_RECIPIENTS

Git git = new Git(this, "cesmarvin")
git.committerName = 'cesmarvin'
git.committerEmail = 'cesmarvin@cloudogu.com'
GitFlow gitflow = new GitFlow(this, git)
GitHub github = new GitHub(this, git)
Changelog changelog = new Changelog(this)

EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "jenkins-gcloud-ces-operations-internal")
Trivy trivy = new Trivy(this, ecoSystem)

parallel(
  "source code": {
    node() { // No specific label
      timestamps {

        def mvnDockerName = '3.6-openjdk-17'
        Maven mvn = new MavenInDocker(this, mvnDockerName)

        stage('Checkout') {
          checkout scm
          git.clean("")
        }

        stage('Check Markdown Links') {
            Markdown markdown = new Markdown(this)
            markdown.check()
        }

        stage('Lint') {
          lintDockerfile()
        }

        stage('Shellcheck') {
          shellCheck("resources/startup.sh")
        }

        stage('Build') {
          setupMaven(mvn)
          // Tui-Editor 1.4.10 has git:// dependencies.
          // Replace git:// with https:// for github dependencies because the unauthenticated protocol is no longer
          // supported.
          sh 'sed -i "s/git:\\/\\/github/https:\\/\\/github/g" yarn.lock'
          mvn 'clean install -DskipTests -Dmaven.wagon.http.pool=false'
          archive '**/target/*.*ar,**/target/*.zip'
        }

        stage('Unit Test') {
          mvn 'test'
        }

        stage('SonarQube') {
          def scannerHome = tool name: 'sonar-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
          withSonarQubeEnv {
            sh "git config 'remote.origin.fetch' '+refs/heads/*:refs/remotes/origin/*'"
            gitWithCredentials("fetch --all")

            if (branch == "master") {
              echo "This branch has been detected as the master branch."
              sh "${scannerHome}/bin/sonar-scanner"
            } else if (branch == "develop") {
              echo "This branch has been detected as the develop branch."
              sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.branch.target=master  "
            } else if (env.CHANGE_TARGET) {
              echo "This branch has been detected as a pull request."
              sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.CHANGE_BRANCH}-PR${env.CHANGE_ID} -Dsonar.branch.target=${env.CHANGE_TARGET} "
            } else if (branch.startsWith("feature/")) {
              echo "This branch has been detected as a feature branch."
              sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.branch.target=develop"
            } else if (branch.startsWith("bugfix/")) {
              echo "This branch has been detected as a bugfix branch."
              sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.branch.target=develop"
            }
          }
          timeout(time: 2, unit: 'MINUTES') { // Needed when there is no webhook for example
            def qGate = waitForQualityGate()
            if (qGate.status != 'OK') {
              unstable("Pipeline unstable due to SonarQube quality gate failure")
            }
          }
        }

        // Archive Unit and integration test results, if any
        junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml,**/target/jest-reports/TEST-*.xml'

        mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
      }
    }
  },
  "dogu-integration": {
    node("vagrant") { // No specific label
      sh 'echo testing dogu integration with ces'
      timestamps {
        properties([
          // Keep only the last 10 build to preserve space
          buildDiscarder(logRotator(numToKeepStr: '10')),
          // Don't run concurrent builds for a branch, because they use the same workspace directory
          disableConcurrentBuilds(),
          parameters([
            booleanParam(defaultValue: true, description: 'Enables cypress to record video of the integration tests.', name: 'EnableVideoRecording'),
            booleanParam(defaultValue: true, description: 'Enables cypress to take screenshots of failing integration tests.', name: 'EnableScreenshotRecording'),
            booleanParam(defaultValue: false, description: 'Test dogu upgrade from latest release or optionally from defined version below', name: 'TestDoguUpgrade'),
            string(defaultValue: '', description: 'Old Dogu version for the upgrade test (optional; e.g. 2.222.1-1)', name: 'OldDoguVersionForUpgradeTest'),
          ])
        ])

        catchError {

          stage('Checkout') {
            checkout scm
            git.clean("")
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

            stage('Build') {
              ecoSystem.build("/dogu")
            }

            stage('Trivy scan') {
              trivy.scanDogu("/dogu", TrivyScanFormat.HTML, TrivyScanLevel.CRITICAL, TrivyScanStrategy.UNSTABLE)
              trivy.scanDogu("/dogu", TrivyScanFormat.JSON, TrivyScanLevel.CRITICAL, TrivyScanStrategy.UNSTABLE)
              trivy.scanDogu("/dogu", TrivyScanFormat.PLAIN, TrivyScanLevel.CRITICAL, TrivyScanStrategy.UNSTABLE)
            }

            stage('Verify') {
              ecoSystem.verify("/dogu")
            }

            stage('Integration tests') {
              ecoSystem.runCypressIntegrationTests([enableVideo          : params.EnableVideoRecording,
                                                    enableScreenshots    : params.EnableScreenshotRecording,
                                                    cypressImage         : "cypress/included:12.17.1",])
            }

            if (params.TestDoguUpgrade != null && params.TestDoguUpgrade) {
              stage('Upgrade dogu') {
                // Remove new dogu that has been built and tested above
                ecoSystem.purgeDogu(doguName)

                if (params.OldDoguVersionForUpgradeTest != '' && !params.OldDoguVersionForUpgradeTest.contains('v')) {
                  println "Installing user defined version of dogu: " + params.OldDoguVersionForUpgradeTest
                  ecoSystem.installDogu("official/" + doguName + " " + params.OldDoguVersionForUpgradeTest)
                } else {
                  println "Installing latest released version of dogu..."
                  ecoSystem.installDogu("official/" + doguName)
                }
                ecoSystem.startDogu(doguName)
                ecoSystem.waitForDogu(doguName)
                ecoSystem.upgradeDogu(ecoSystem)

                // Wait for upgraded dogu to get healthy
                ecoSystem.waitForDogu(doguName)
              }
            }

            if (gitflow.isReleaseBranch()) {
              String releaseVersion = git.getSimpleBranchName()

              stage('Finish Release') {
                gitflow.finishRelease(releaseVersion)
              }

              stage('Push Dogu to registry') {
                ecoSystem.push("/dogu")
              }

              stage('Add Github-Release') {
                github.createReleaseWithChangelog(releaseVersion, changelog)
              }
            }
          } finally {
            stage('Clean') {
              ecoSystem.destroy()
            }
          }
        }

        mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
      }
    }
  }
)

def setupMaven(mvn) {
  if ("master".equals(env.BRANCH_NAME)) {
    mvn.additionalArgs = "-DperformRelease"
    currentBuild.description = mvn.getVersion()
  }
}

void gitWithCredentials(String command) {
  withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
    sh(
      script: "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" " + command,
      returnStdout: true
    )
  }
}
