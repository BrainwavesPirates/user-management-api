node {
	//Define all variables
	def app1_name = 'user-management-api'
	def app1_image_tag = "${env.REPOSITORY}/${app1_name}:v${env.BUILD_NUMBER}"
	def app1_dockerfile_name = 'Dockerfile-userManagementApi'
	def app1_container_name = 'user-management-api'
	
	//Stage 1: Checkout Code from Git
	stage('Application Code Checkout from Git') {
		checkout scm
		
	}
	

	//Stage 5: Build Docker Image	
	stage('Build Docker Image') {
		container('docker'){
			sh("docker build -f ${app1_dockerfile_name} -t ${app1_image_tag} .")
		}
		
	}

	//Stage 6: Push the Image to a Docker Registry
	stage('Push Docker Image to Docker Registry') {
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

	//Stage 7: Deploy Application on K8s
	stage('Deploy Application on K8s') {
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
