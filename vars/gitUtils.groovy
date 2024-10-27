def cloneRepo(String repoUrl, String branch = 'main', String credentialsId = '') {
    if (credentialsId) {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: repoUrl, credentialsId: credentialsId]]
        ])
    } else {
        checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: repoUrl]]
        ])
    }
    echo "Cloned repository ${repoUrl} on branch ${branch}"
}

def pushChanges(String credentialsId = '', String commitMessage = 'Automated commit') {
    sh "git add ."
    sh "git commit -m '${commitMessage}'"
    if (credentialsId) {
        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
            sh "git push https://${GIT_USER}:${GIT_PASS}@\$(git config --get remote.origin.url | sed 's/https:\\/\\///g') HEAD:$(git rev-parse --abbrev-ref HEAD)"
        }
    } else {
        sh "git push origin $(git rev-parse --abbrev-ref HEAD)"
    }
    echo "Changes pushed to the repository."
}
