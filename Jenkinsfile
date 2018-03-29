pipeline {
    agent {
//        docker {
//            image 'jenkins/ssh-slave'
//            label 'docker-enabled'
//            args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/jenkins_remote_root/.gradle/wrapper/dists:/root/.gradle/wrapper/dists -v $HOME/.m2:/root/.m2'
//        }
        node { label 'docker-enabled' }
    }

    environment {
        PIPELINE_BUILD_ID = "${GIT_BRANCH}-${BUILD_NUMBER}"
        DOCKERHUB_CREDS = credentials("dockerhub")
        // implicit DOCKERHUB_CREDS_USR
        // implicit DOCKERHUB_CREDS_PSW
    }

    stages {
        stage('Commit Stage') {
            steps {
                sh './gradlew clean build buildDockerImage'
                sh '''\
                docker login -u $DOCKERHUB_CREDS_USR -p $DOCKERHUB_CREDS_PSW
                docker push qu4rk/thesis-zuulservice:$PIPELINE_BUILD_ID
                '''
            }
        }
        stage('Acceptance Stage') {
            steps {
                echo 'TODO Acceptance Stage'
            }
        }
        stage('Performance Stage') {
            steps {
                echo 'TODO Performance Stage'
            }
        }
        stage('UAT Stage') {
            steps {
                echo 'TODO Acceptance Stage'
            }
        }
        stage('Deploy to Production') {
            steps {
//                input "Proceed?"
                echo 'TODO deploy to Production'
            }
        }

    }

//    post {
//        always {
//             junit 'build/reports/**/*.xml'
//        }
//    }
}
