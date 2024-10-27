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
    sh "git config --global user.email jenkins@test.com"
    sh "git config --global user.name jenkins"
    sh "git commit -m '${commitMessage}'"
    
    if (credentialsId) {
        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
            // Build the command as a Groovy string
            def remoteUrl = "\$(git config --get remote.origin.url | sed 's/https:\\/\\///g')"
            def branchName = "\$(git rev-parse --abbrev-ref HEAD)"
            def pushCommand = "git push https://${GIT_USER}:${GIT_PASS}@${remoteUrl} HEAD:${branchName}"
            
            // Execute the command
            sh pushCommand
        }
    } else {
        sh "git push origin \$(git rev-parse --abbrev-ref HEAD)"
    }
    echo "Changes pushed to the repository."
}
