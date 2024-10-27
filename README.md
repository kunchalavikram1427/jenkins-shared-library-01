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
### Example 02
```
@Library('shared_lib') _  // Load the shared library

pipeline {
    agent any
    environment {
        GIT_REPO = 'https://github.com/kunchalavikram1427/maven-employee-web-application.git'
        GIT_CREDENTIALS = ''            // Make credentials optional
        DOCKER_IMAGE = 'my-docker-image'
        DOCKER_TAG = '1.0'
        DOCKER_CREDENTIALS = ''         // Make credentials optional
    }
    stages {
        stage('Clone Repository') {
            steps {
                script {
                    gitUtils.cloneRepo(env.GIT_REPO, 'master', env.GIT_CREDENTIALS)
                }
            }
        }
        stage('Add a test file Repository') {
            steps {
                script {
                    cat "Hello" > hello.txt
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    dockerUtils.buildImage(
                        imageName: env.DOCKER_IMAGE,
                        tag: env.DOCKER_TAG,
                        dockerfileName: Dockerfile.multistage
                    )
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    dockerUtils.pushImage(env.DOCKER_IMAGE, env.DOCKER_TAG, env.DOCKER_CREDENTIALS)
                }
            }
        }
        stage('Push Changes to Git') {
            steps {
                script {
                    gitUtils.pushChanges(env.GIT_CREDENTIALS, 'Automated Docker build and push commit')
                }
            }
        }
    }
}
```