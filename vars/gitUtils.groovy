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

    // Construct the branch name using the Jenkins build number
    def featureBranch = "feature-${env.BUILD_NUMBER}"

    if (credentialsId) {
        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
            // Build the command as a Groovy string
            def remoteUrl = "\$(git config --get remote.origin.url | sed 's/https:\\/\\///g')"
            def pushCommand = "git push https://${GIT_USER}:${GIT_PASS}@${remoteUrl} HEAD:refs/heads/${featureBranch}"
            
            // Execute the command
            sh pushCommand
        }
    } else {
        sh "git push origin HEAD:refs/heads/${featureBranch}"
    }
    
    echo "Changes pushed to branch '${featureBranch}'."
    return featureBranch // Return the branch name for later use
}


def createPullRequest(String default_branch, String featureBranch) {
    def prTitle = "Merge ${featureBranch} into master"
    def prBody = "This PR merges the feature branch into master."

    // Using GitHub CLI to create the PR
    sh """
        gh pr create \
            --base ${default_branch} \
            --head ${featureBranch} \
            --title "${prTitle}" \
            --body "${prBody}" \
            --repo ${env.GIT_REPO}
    """
    echo "Pull request created from '${featureBranch}' to ${default_branch}."
}
