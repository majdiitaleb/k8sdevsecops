pipeline {
  agent any
  stages {
      stage('Build Artifact') {
            steps {
            withMaven(maven: 'Apache Maven 3.8.8'){
            sh "mvn -B clean package -DskipTests=true"
                          archive 'target/*.jar'
            }

            }
        }

}
}