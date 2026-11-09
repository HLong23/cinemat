// Pipeline CI/CD chạy trên Jenkins cài local (Windows hoặc Linux).
// Yêu cầu: Jenkins có JDK 17 + Maven đã khai báo trong "Global Tool Configuration"
// với tên 'JDK17' và 'Maven3'; (tuỳ chọn) Docker nếu muốn deploy bằng container.
pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    parameters {
        choice(name: 'DEPLOY_MODE', choices: ['docker', 'none'],
               description: 'docker: build image và chạy bằng docker compose; none: chỉ build + test')
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps { script { run('mvn -B clean compile') } }
        }

        stage('Test') {
            steps { script { run('mvn -B test') } }
            post {
                always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' }
            }
        }

        stage('Package') {
            steps { script { run('mvn -B package -DskipTests') } }
            post {
                success { archiveArtifacts artifacts: 'target/*.jar', fingerprint: true }
            }
        }

        stage('Deploy (local)') {
            when { expression { params.DEPLOY_MODE == 'docker' } }
            steps { script { run('docker compose up -d --build') } }
        }
    }

    post {
        success { echo 'Build & deploy thành công: http://localhost:8080' }
        failure { echo 'Pipeline thất bại - xem log ở các stage phía trên.' }
    }
}

def run(String cmd) {
    if (isUnix()) { sh cmd } else { bat cmd }
}
