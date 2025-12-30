pipeline {
    agent any

    tools {
        jdk 'jdk17' // Configurato in Jenkins → Manage Jenkins → Tools
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew --version'
            }
        }

        stage('Clean') {
            steps {
                sh './gradlew clean'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Publish') {
            when {
                branch 'main'
            }
            environment {
                MAVEN_CREDS = credentials('maven-repo')
            }
            steps {
                sh '''
                    ./gradlew publish \
                      -PmavenUser=$MAVEN_CREDS_USR \
                      -PmavenPassword=$MAVEN_CREDS_PSW
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Build completata con successo'
        }

        failure {
            echo '❌ Build fallita'
        }

        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            cleanWs()
        }
    }
}
