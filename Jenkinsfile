pipeline {
  agent any

 environment {
    deploymentName = "devsecops"
    containerName = "devsecops-container"
    serviceName = "devsecops-svc"
    imageName = "majditaleb/numeric-app:${GIT_COMMIT}"
    applicationURL="http://desecops-majdi.eastus.cloudapp.azure.com"
    applicationURI="/increment/99"
  }

  stages {
      stage('Build Artifact') {
            steps {

            sh "mvn -B clean package -DskipTests=true"
                          archive 'target/*.jar'


            }
        }
      stage('Units tests') {
        steps {

          sh "mvn test"


        }
      }

          stage('sonarqube analysis') {
                 steps {
                      withSonarQubeEnv('SonarQube') {

                         sh "mvn clean verify   sonar:sonar -Dsonar.projectKey=numeric-application"
                      }
                       timeout(time: 2, unit: 'MINUTES'){
                         script {
                                waitForQualityGate abortPipeline: true
                         }
                       }
                 }
              }
                stage('Vulnerability scan -docker') {
                          steps {
                          parallel(
                          "dependency scan": {
                          sh "${mvnHome}/bin/mvn dependency-check:check"
                          },
                          "Trivy scan":{
                              sh "bash trivy-docker-image-scan.sh"
                          },
                        
                          )
                          }
                  }




       stage('Docker build and push') {
                 steps {
                 withDockerRegistry(credentialsId: "docker-hub", url: "") {
                   sh 'printenv'
                   sh 'sudo docker build -t majditaleb/numeric-app:""$GIT_COMMIT"" .'
                   sh 'docker push  majditaleb/numeric-app:""$GIT_COMMIT""'
                   }
                 }
             }
               stage('Vulnerability Scan - Kubernetes') {
                         steps {
                            parallel(
                              "OPA Scan": {
                                 sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
                               },
                               "Kubesec Scan": {
                                sh "bash kubesec-scan.sh"
                              },
                           "Trivy Scan": {
                             sh "bash trivy-k8s-scan.sh"
                           }
                           )
                        }
                       }

               stage('K8S Deployment - DEV') {
                             steps {

                                parallel(
                                              "Deployment": {
                                                withKubeConfig([credentialsId: 'kubeconfig']) {
                                                  sh "bash k8s-deployment.sh"
                                                }
                                              },
                                              "Rollout Status": {
                                                withKubeConfig([credentialsId: 'kubeconfig']) {
                                                  sh "bash k8s-deployment-rollout-status.sh"
                                                }
                                              }
                                            )


                             }
                           }

}
}