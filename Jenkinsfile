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
            }
            )
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
    post {

    always  {
         junit 'target/surefire-reports/*.xml'
         jacoco execPattern: 'target/jacoco.exec'
         pitmutation  mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
        // dependencyCheckPublisher   pattern: 'target/dependency-check-report.xml'
          }
       }
