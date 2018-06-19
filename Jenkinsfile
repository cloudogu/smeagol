#!groovy
@Library('github.com/cloudogu/ces-build-lib@ac17d45')
import com.cloudogu.ces.cesbuildlib.*

node() { // No specific label

    properties([
            // Keep only the last 10 build to preserve space
            buildDiscarder(logRotator(numToKeepStr: '10')),
            // Don't run concurrent builds for a branch, because they use the same workspace directory
            disableConcurrentBuilds()
    ])

    String defaultEmailRecipients = env.EMAIL_RECIPIENTS

    catchError {

        Maven mvn = new MavenWrapper(this)
        Git git = new Git(this)

        stage('Checkout') {
            checkout scm
            git.clean("")
        }

        stage('Build') {
            setupMaven(mvn)
            mvn 'clean install -DskipTests'
            archive '**/target/*.*ar,**/target/*.zip'
        }

        stage('Unit Test') {
            mvn 'test'
        }

        stage('SonarQube') {
            def sonarQube = new SonarQube(this, 'ces-sonar')
            sonarQube.updateAnalysisResultOfPullRequestsToGitHub('sonarqube-gh-token')

            sonarQube.analyzeWith(mvn)
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