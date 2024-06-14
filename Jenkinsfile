pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          userRemoteConfigs: [[
                                                      url: 'https://github.com/bartoszstelmach0/ProjectManagmentSystem.git',
                                                      credentialsId: 'projectManagment-github-token'
                                              ]]
                ])
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test -X'  //  tryb debugowania
                sh 'ls -la target/surefire-reports'
                sh 'cat target/surefire-reports/TEST-*.xml'
            }

            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish') {
            steps {
                sh './mvnw package'
            }
            post {
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    }
}
