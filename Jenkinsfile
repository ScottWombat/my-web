pipeline {
    agent { 
        label 'agent-1'
    }
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "192.168.62.188:8081"
        NEXUS_REPOSITORY = "maven-nexus-repo"
        NEXUS_CREDENTIAL_ID = "nexus-user-credentials"
        PACKAGING_NAME = "my-web.web"
    }

    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true package' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }

        stage("Publish to Nexus Repository Manager") {
            
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: pom.version,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging],
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: "pom.xml",
                                type: "pom"]
                            ]
                        );
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
        
        stage('Pull artifacts & deploy on tomcat') {
            steps{
                  withCredentials([usernamePassword(credentialsId: 'my-tomcat-cred',
                                      usernameVariable: 'USERNAME',
                                      passwordVariable: 'PASSWORD')]) {
                    sh "mkdir -p ${env.WORKSPACE}/dump"                 
                    sh 'curl -u ' + USERNAME + ':' + PASSWORD + ' -X GET "http://192.168.62.188:8081/repository/maven-nexus-repo/com/myweb/app/my-web/1.0/my-web-1.0.war" --output ${env.WORKSPACE}/dump/my-web.war'
                    sh 'scp ${env.WORKSPACE}/dump/my-web.war revit@192.168.62.203:/opt/tomcat/webapps/'
            }
          }
       }
       
    }
   
}