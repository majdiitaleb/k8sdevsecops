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





       stage('Docker build and push') {
                 steps {
                 withDockerRegistry(credentialsId: "docker-hub", url: "") {
                   sh 'printenv'
                   sh 'sudo docker build -t majditaleb/numeric-app:""$GIT_COMMIT"" .'
                   sh 'docker push  majditaleb/numeric-app:""$GIT_COMMIT""'
                   }
                 }
             }

}
}