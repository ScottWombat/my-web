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
        
        stage('Pull artifacts from Nexus') {
            steps{
                withCredentials([usernamePassword(credentialsId: 'my-tomcat-cred',
                                      usernameVariable: 'USERNAME',
                                      passwordVariable: 'PASSWORD')]) {        
                    sh 'curl -u ' + USERNAME + ':' + PASSWORD + ' -X GET "http://192.168.62.188:8081/repository/maven-nexus-repo/com/myweb/app/my-web/1.0/my-web-1.0.war" --output my-web.war'
               }
           }
       }

       stage('Deploy war on Tomcat'){
            //steps{
            //     sh 'scp ${WORKSPACE}/my-web.war revit@192.168.62.203:/opt/tomcat/webapps'
            //}
           steps{
                sshagent(credentials : ['new_cred1']) {
                   sh 'scp ${WORKSPACE}/my-web.war revit@192.168.62.203:/opt/tomcat/webapps'
                }
           }
       }
       //stage("deploy-dev"){
        //             steps{
        //                sshagent(['user-id-tomcat-deployment']) {
        //                sh """
        //                scp -o StrictHostKeyChecking=no target/UI.war
        //                root@192.168.1.000:/opt/tomcat/webapps/
        //                ssh root@192.168.1.000 /opt/tomcat/bin/shutdown.sh
        //                ssh root@192.168.1.000 /opt/tomcat/bin/startup.sh
        //                 """
        //                  }
        //                }
      // }
       stage("Build docker"){
            steps{
                sh 'docker build -t  scottwombat/myweb-1:1.0.0 .'
            }
       }

       stage("Push container to Docker hub"){
            steps{
                withCredentials([usernamePassword(credentialsId: 'docker_credential',
                                      usernameVariable: 'USERNAME',
                                      passwordVariable: 'PASSWORD')]){
                    sh 'echo ' + PASSWORD + ' | docker login -u ' + USERNAME + ' --password-stdin docker.io'
                    //sh 'docker login -u ' + USERNAME + '-p ' + PASSWORD
                    sh 'docker push scottwombat/myweb-1:1.0.0'
                }
            }
       }

       stage("Run docker"){
            steps{
+                sh "echo hello"
            }
       }
       
    }
   
}