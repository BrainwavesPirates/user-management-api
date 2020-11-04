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
  
}
