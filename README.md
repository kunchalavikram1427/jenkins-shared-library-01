# Jenkins Shared Library 01

## Folder structure
```
example_shared_library/
├── src/
│   └── org/
│       └── dme/
│           ├── *.groovy
│           └── Bar.groovy
├── vars/
│   ├── *.groovy
│   ├── fooBar.groovy 
│   └── foo.txt
└── resources/
    └── org/
        └── dme/
            ├── *.json
            ├── *.html
            ├── *.png
            └── bar.json
```

## Concepts

### call() function

In Jenkins Shared Libraries, the `call()` function allows you to treat a Groovy file in the `vars/` directory as if it were a function in your pipeline. By defining a `call()` method in the file, you can invoke it directly using the filename, making parameter passing simple without explicitly referencing the method name. This simplifies and streamlines custom pipeline steps.

## Example Pipelines

### Example 01
```
@Library('shared_lib') _  // Load the shared library with the name 'shared-lib'

pipeline {
    agent any
    stages {
        stage('Test exampleFunction') {
            steps {
                // Call the function defined in vars/exampleFunction.groovy
                exampleFunction('DevOps Enthusiast')
            }
        }
        stage('Test MyClass') {
            steps {
                script {
                    // Use the custom class defined in src/org/dme/MyClass.groovy
                    def greeting = org.dme.MyClass.getGreeting('DevOps Made Easy')
                    echo greeting
                }
            }
        }
    }
}
```
You can also use `import`
```
@Library('shared_lib') _  // Load the shared library with the name 'shared-lib'

import org.dme.MyClass

pipeline {
    agent any
    stages {
        stage('Test exampleFunction') {
            steps {
                // Call the function defined in vars/exampleFunction.groovy
                exampleFunction('DevOps Enthusiast')
            }
        }
        stage('Test MyClass') {
            steps {
                script {
                    // Use the custom class defined in src/org/dme/MyClass.groovy
                    def greeting = MyClass.getGreeting('DevOps Made Easy')
                    echo greeting
                }
            }
        }
    }
}
```
### Example 02
```
@Library('shared_lib') _

pipeline {
    agent any
    environment {
        GIT_REPO = 'https://github.com/kunchalavikram1427/maven-employee-web-application.git'
        GIT_DEFAULT_BRANCH = "master"
        GIT_CREDENTIALS = 'git_creds'
    }
    stages {
        stage('Clone Repository') {
            steps {
                script {
                    gitUtils.cloneRepo(env.GIT_REPO, env.GIT_DEFAULT_BRANCH, env.GIT_CREDENTIALS)
                }
            }
        }
        stage('Make Changes') {
            steps {
                script {
                    sh 'echo "Hello" > hello.txt'
                }
            }
        }
        stage('Push Changes') {
            steps {
                script {
                    // Push changes and store the feature branch name
                    def featureBranch = gitUtils.pushChanges(env.GIT_CREDENTIALS, 'Updated hello.txt with greeting')
                    
                    // Create the pull request from the feature branch
                    gitUtils.createPullRequest(env.GIT_DEFAULT_BRANCH, featureBranch, env.GIT_CREDENTIALS)
                }
            }
        }
    }
}
```

### Example 03
```
@Library('shared_lib') _

pipeline {
    agent any
    stages {
        stage('Get Single User Info') {
            steps {
                script {
                    def username = 'alice'  // Specify the username you want to fetch info for
                    def singleUserInfo = getUserInfo(username)  // Call the function for single user
                    echo "Single User Info: ${singleUserInfo}"
                }
            }
        }
        stage('Get Multiple Users Info') {
            steps {
                script {
                    def usernames = ['alice', 'bob', 'eve']  // List of usernames
                    def userInfoList = getUserInfo.getMultiUserInfo(usernames)  // Call the function for multiple users
                    echo "Multiple Users Info: ${userInfoList}"
                }
            }
        }
    }
}
```
