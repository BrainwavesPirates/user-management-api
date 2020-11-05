pipeline {
  environment {
    dockerHome = tool "MyDocker"
    PATH = "${dockerHome}/bin:${env.PATH}"
    app1_name = "user-management-api"
    app1_image_tag = "${env.REPOSITORY}/${app1_name}:v${env.BUILD_NUMBER}"
    app1_dockerfile_name = "Dockerfile-userManagementApi"
    app1_container_name = "user-management-api"
  }
  
  tools {  
   maven 'MyMaven'  
  }
  
  agent {
    kubernetes {
      label 'user-management-api'
      defaultContainer 'jnlp'
      yaml """
      apiVersion: v1
      kind: Pod
      metadata:
      labels:
        component: ci
      spec:
        # Use service account that can deploy to all namespaces
        serviceAccountName: default
        containers:
        - name: maven
          image: maven:latest
          command:
          - cat
          tty: true
          volumeMounts:
            - mountPath: "/root/.m2"
              name: m2
        - name: docker
          image: docker:latest
          command:
          - cat
          tty: true
          volumeMounts:
          - mountPath: /var/run/docker.sock
            name: docker-sock
        volumes:
          - name: docker-sock
            hostPath:
              path: /var/run/docker.sock
          - name: m2
            persistentVolumeClaim:
              claimName: m2
    """
    }
  }

  stages {

      stage('Application Code Checkout from Git') {
        steps{
          checkout scm
        }
      }

      //Stage 4: Build with mvn
      stage('Build with Maven') {
        steps{
            script {
                  sh ("mvn -B -DskipTests clean package")
            }
        }
      }
    
      stage("Build image") {
        steps{
          script {
            container('docker'){
              sh("docker build -f ${app1_dockerfile_name} -t ${app1_image_tag} .")
            }
          }
        }
      }

    
      //Stage 6: Push the Image to a Docker Registry
      stage('Push Docker Image to Docker Registry') {
        steps{
          script {
            container('docker'){
              withCredentials([[$class: 'UsernamePasswordMultiBinding',
              credentialsId: env.DOCKER_CREDENTIALS_ID,
              usernameVariable: 'USERNAME',
              passwordVariable: 'PASSWORD']]) {
                docker.withRegistry(env.DOCEKR_REGISTRY, env.DOCKER_CREDENTIALS_ID) {
                  sh("docker push ${app1_image_tag}")
                }
             }
           }
         }
       }
        
      }

    
      //Stage 7: Deploy Application on K8s
      stage('Deploy Application on K8s') {
         steps{
          script {
            container('kubectl'){
              withKubeConfig([credentialsId: env.K8s_CREDENTIALS_ID,
              serverUrl: env.K8s_SERVER_URL,
              contextName: env.K8s_CONTEXT_NAME,
              clusterName: env.K8s_CLUSTER_NAME]){
                sh("kubectl apply -f ${app1_name}.yml")
                sh("kubectl set image deployment/${app1_name} ${app1_container_name}=${app1_image_tag}")
              }     
            }
         }
       }
      }

  }

  }
