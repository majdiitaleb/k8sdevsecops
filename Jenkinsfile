pipeline {
  agent any

  stages {
      stage('Build Artifact') {
            steps {
              sh "mvn clean package -DskipTests=true"
              archive 'target/*.jar'
            }
        }
      stage('Units tests') {
                    steps {
                      sh "mvn test"
                    }
                   post {
                    always {
                      junit 'target/surefire-reports/*.xml'
                      jacoco execPattern: 'target/jacoco.exec'
                    }
                   }
                }
                 stage('Mutation tests') {
                                    steps {
                                      sh "mvn org.pitest:pitest-maven:mutationCoverage"
                                    }
                                   post {
                                      always  {
                                        pitmutation  mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
                                      }
                                    }
                                }
              stage('sonarqube analysis') {
                         steps {
                           withSonarQubeEnv('My SonarQube Server') {
                           sh "mvn clean verify sonar:sonar -Dsonar.projectKey=numerci-application    -Dsonar.host.url=http://devsecops-demo-k8s.eastus.cloudapp.azure.com:9000   -Dsonar.token=sqp_fcb8535df4f9be414ec99f322b43094e9aafecce"
                         }
                         timeout(tome: 2, unit: 'MINUTES'){
                          scritp {
                            waitForQualityGate abortPipeline: true
                          }
                         }
                     }
              }
       stage('Docker build and push') {
            steps {
            withDockerRegistry(credentialsId: "docker-hub", url: "") {
              sh 'printenv'
              sh 'docker build -t majditaleb/numeric-app:""$GIT_COMMIT"" .'
              sh 'docker push  majditaleb/numeric-app:""$GIT_COMMIT""'
              }
            }
        }
         stage('Kubernete deploiement -dev') {
            steps {
            withKubeConfig([credentialsId: "kubeconfig"]) {
              sh "sed -i 's#replace#majditaleb1/numeric-app:${GIT_COMMIT}#' k8s_deployment_service.yaml"
              sh "kubectl apply -f k8s_deployment_service.yaml"
              }
            }
        }

    }
}