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
                                    post{
                                      always{
                                        pitmutation  mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
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
              sh "sed -i 's#replace#majditaleb/numeric-app:${GIT_COMMIT}#' k8s_deployment_service.yaml"
              sh "kubectl apply -f k8s_deployment_service.yaml"
              }
            }
        }

    }
}