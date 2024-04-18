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

                  sh "mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=numeric-application \
                        -Dsonar.projectName='numeric-application' \
                        -Dsonar.host.url=http://devsecops-majdi.eastus.cloudapp.azure.com:9000 \
                        -Dsonar.token=sqp_90421e855d2a444b6a597afd8bbd374ea8f58305"


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