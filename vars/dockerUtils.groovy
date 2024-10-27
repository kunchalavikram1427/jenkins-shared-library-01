def buildImage(String imageName, String tag = 'latest', String dockerfilePath = '.') {
    sh "docker build -t ${imageName}:${tag} -f ${dockerfilePath}/Dockerfile ${dockerfilePath}"
    echo "Built Docker image ${imageName}:${tag}"
}

def pushImage(String imageName, String tag = 'latest', String registryCredentialsId = '') {
    if (registryCredentialsId) {
        withCredentials([usernamePassword(credentialsId: registryCredentialsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
        }
    }
    sh "docker push ${imageName}:${tag}"
    echo "Pushed Docker image ${imageName}:${tag} to registry."
}