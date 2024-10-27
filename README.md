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

## Pipeline
```
@Library('shared-lib') _  // Load the shared library with the name 'shared-lib'

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
                    // Use the custom class defined in src/org/example/MyClass.groovy
                    def greeting = org.example.MyClass.getGreeting('Developer')
                    echo greeting
                }
            }
        }
    }
}
```