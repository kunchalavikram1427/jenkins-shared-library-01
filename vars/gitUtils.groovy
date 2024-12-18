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


def createPullRequest(String defaultBranch, String featureBranch, String credentialsId) {
    def prTitle = "Merge ${featureBranch} into ${defaultBranch}"
    def prBody = "This PR merges the feature branch into ${defaultBranch}."
    
    // Extract the owner and repo from GIT_REPO
    def repoParts = env.GIT_REPO.replaceAll(/\.git$/, '').split('/')
    def owner = repoParts[-2]
    def repo = repoParts[-1]

    // Get GitHub credentials
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        // Construct the JSON payload for the PR
        def jsonPayload = """{
            "title": "${prTitle}",
            "body": "${prBody}",
            "head": "${featureBranch}",
            "base": "${defaultBranch}"
        }"""

        // Make the API call to create the PR with basic authentication
        sh """
            curl -L -X POST \
                -u ${GIT_USER}:${GIT_PASS} \
                -H "Accept: application/vnd.github+json" \
                -H "X-GitHub-Api-Version: 2022-11-28" \
                -d '${jsonPayload}' \
                https://api.github.com/repos/${owner}/${repo}/pulls
        """
    }
    
    echo "Pull request created from '${featureBranch}' to '${defaultBranch}'."
}
