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
            withMaven(maven: 'Apache Maven 3.8.8'){
            sh "mvn -B clean package -DskipTests=true"
                          archive 'target/*.jar'
            }

            }
        }
      stage('Units tests') {
        steps {
         withMaven(maven: 'Apache Maven 3.8.8'){
          sh "mvn test"
         }

        }
      }
      stage('Mutation tests') {
         steps {
         withMaven(maven: 'Apache Maven 3.8.8'){
                sh "mvn org.pitest:pitest-maven:mutationCoverage"
                }
         }

      }
       stage('sonarqube analysis') {
          steps {
               withSonarQubeEnv('SonarQube') {

                  sh "${mvnHome}/bin/mvn  sonar:sonar -Dsonar.projectKey=numeric-app"
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
            "OPA Conftest":{
            	sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-docker-security.rego Dockerfile --all-namespaces'
            		}
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
                  stage('Integration Tests - DEV') {
      steps {
        script {
          try {
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh "bash integration-test.sh"
            }
          } catch (e) {
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh "kubectl -n default rollout undo deploy ${deploymentName}"
            }
            throw e
          }
        }
      }
    }
      
    stage('OWASP ZAP - DAST') {
      steps {
        withKubeConfig([credentialsId: 'kubeconfig']) {
          sh 'bash zap.sh'
        }
      }
    }
    
    stage('Prompte to PROD?') {
      steps {
        timeout(time: 2, unit: 'DAYS') {
          input 'Do you want to Approve the Deployment to Production Environment/Namespace?'
        }
      }
    }

   /* stage('K8S CIS Benchmark') {
      steps {
        script {

          parallel(
            "Master": {
              sh "bash cis-master.sh"
            },
            "Etcd": {
              sh "bash cis-etcd.sh"
            },
            "Kubelet": {
              sh "bash cis-kubelet.sh"
            }
          )

        }
      }
    }*/

    stage('K8S Deployment - PROD') {
      steps {
        parallel(
          "Deployment": {
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh "sed -i 's#replace#${imageName}#g' k8s_PROD-deployment_service.yaml"
              sh "kubectl -n prod apply -f k8s_PROD-deployment_service.yaml"
            }
          },
          "Rollout Status": {
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh "bash k8s-PROD-deployment-rollout-status.sh"
            }
          }
        )
      }
    }

    
  }
    post {

    always  {
         junit 'target/surefire-reports/*.xml'
         jacoco execPattern: 'target/jacoco.exec'
         pitmutation  mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
        dependencyCheckPublisher   pattern: 'target/dependency-check-report.xml'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'owasp-zap-report', reportFiles: 'zap_report.html', reportName: 'OWASP ZAP HTML Report', reportTitles: 'OWASP ZAP HTML Report', useWrapperFileDirectly: true])
          }
       }

}