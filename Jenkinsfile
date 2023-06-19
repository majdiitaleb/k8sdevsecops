pipeline {
  agent any

 environment {
    deploymentName = "devsecops"
    containerName = "devsecops-container"
    serviceName = "devsecops-svc"
    imageName = "majditaleb/numeric-app:${GIT_COMMIT}"
    applicationURL="http://devsecops-demo-k8s.eastus.cloudapp.azure.com"
    applicationURI="/increment/99"
  }
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
      }
      stage('Mutation tests') {
         steps {
                sh "mvn org.pitest:pitest-maven:mutationCoverage"
         }

      }
       stage('sonarqube analysis') {
          steps {
               withSonarQubeEnv('SonarQube') {
                  sh "mvn  sonar:sonar -Dsonar.projectKey=numerci-application  -Dsonar.host.url=${env.SONAR_HOST} "
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
            sh "mvn dependency-check:check"
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
              sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
                //parallel(
                 //"OPA Scan": {
                   // sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
                  //},
                  //"Kubesec Scan": {
                   //  sh "bash kubesec-scan.sh"
                 //},
                //"Trivy Scan": {
                   //  sh "bash trivy-k8s-scan.sh"
                 // }
               // )
               }
            }
         stage('K8S Deployment - DEV') {
                steps {

                 withKubeConfig([credentialsId: 'kubeconfig']) {
                                               sh "sed -i 's#replace#majditaleb/numeric-app:${GIT_COMMIT}#g' k8s_deployment_service.yaml"
                                               sh "kubectl  apply -f k8s_deployment_service.yaml"
                                              }

                }
              }
  }
    post {

    always  {
         junit 'target/surefire-reports/*.xml'
         jacoco execPattern: 'target/jacoco.exec'
         pitmutation  mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
        // dependencyCheckPublisher   pattern: 'target/dependency-check-report.xml'
          }
       }

}