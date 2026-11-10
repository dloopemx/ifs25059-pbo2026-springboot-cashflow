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
                sh './mvnw -B -Pproduction -DskipTests clean package'
            }
        }
        stage('Task') {
            steps {
                sh './mvnw -B test'
            }
        }
        stage('Deploy') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                echo 'JAR aplikasi berhasil dibuat dan diarsipkan. Deploy ke website Delcom harus mengikuti Jenkinsfile resmi dari halaman Submission CI/CD.'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Build dan pengujian selesai.'
        }
        failure {
            echo 'Pipeline gagal. Periksa tahap pertama yang berwarna merah dan log Maven.'
        }
    }
}
