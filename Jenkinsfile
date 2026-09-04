pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['qa', 'dev', 'staging'], description: 'Target environment')
        choice(name: 'TEST_SUITE', choices: ['order-service-tests', 'e2e-workflow-tests', 'all'], description: 'Test module to run')
        choice(name: 'GROUPS', choices: ['smoke', 'regression', 'e2e', ''], description: 'TestNG groups filter (empty = all)')
        string(name: 'CORE_VERSION', defaultValue: '1.0.0-SNAPSHOT', description: 'api-test-core artifact version')
        string(name: 'CORE_REPO_URL', defaultValue: '', description: 'Git URL for api-test-core repo (optional if core is in Maven repo)')
        string(name: 'PARALLEL_THREADS', defaultValue: '4', description: 'Parallel test threads')
    }

    environment {
        JAVA_HOME = tool name: 'jdk-21', type: 'jdk'
        MAVEN_HOME = tool name: 'maven-3.9', type: 'Maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
        AUTH_CLIENT_ID = credentials('auth-client-id')
        AUTH_CLIENT_SECRET = credentials('auth-client-secret')
        ORDER_DB_USER = credentials('order-db-user')
        ORDER_DB_PASSWORD = credentials('order-db-password')
        REDIS_PASSWORD = credentials('redis-password')
    }

    stages {
        stage('Checkout Services') {
            steps {
                checkout scm
            }
        }

        stage('Build & Install Core') {
            when {
                expression { params.CORE_REPO_URL?.trim() }
            }
            steps {
                dir('api-test-core') {
                    git url: params.CORE_REPO_URL, branch: 'main'
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Compile') {
            steps {
                sh """
                    mvn clean compile -DskipTests \
                        -Dapi-test-core.version=${params.CORE_VERSION} \
                        -Denv=${params.ENV}
                """
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def groupsArg = params.GROUPS?.trim() ? "-Pfiltered -Dtestng.groups=${params.GROUPS}" : ''
                    def modules = params.TEST_SUITE == 'all'
                        ? '-pl test-suites/order-service-tests,test-suites/e2e-workflow-tests -am'
                        : "-pl test-suites/${params.TEST_SUITE} -am"

                    sh """
                        mvn test ${modules} \
                            -Denv=${params.ENV} \
                            -Dparallel.threads=${params.PARALLEL_THREADS} \
                            -Dapi-test-core.version=${params.CORE_VERSION} \
                            ${groupsArg}
                    """
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: '**/target/allure-results/**', allowEmptyArchive: true
                }
            }
        }

        stage('Allure Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'test-suites/order-service-tests/target/allure-results'], [path: 'test-suites/e2e-workflow-tests/target/allure-results']]
            }
        }
    }

    post {
        failure {
            echo 'API test pipeline failed. Check Allure report and surefire logs.'
        }
    }
}
