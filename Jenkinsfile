pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh '''
                    ./mvnw -B \
                    -Dmaven.repo.local="$WORKSPACE/.m2/repository" \
                    -Pproduction \
                    -DskipTests \
                    clean package
                '''
            }
        }

        stage('Task') {
            steps {
                sh '''
                    ./mvnw -B \
                    -Dmaven.repo.local="$WORKSPACE/.m2/repository" \
                    test
                '''
            }
        }

        stage('Deploy') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                echo 'JAR aplikasi berhasil dibuat dan diarsipkan.'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
        }

        success {
            echo 'Build dan pengujian selesai.'
        }

        failure {
            echo 'Pipeline gagal. Periksa log tahap yang gagal.'
        }
    }
}
