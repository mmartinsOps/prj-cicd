def createS2IBinary(namespace, appName, environment, image) {
    openshift.withCluster() {
        openshift.withProject("${namespace}") {             
            // image -> s2i image that will build the application container image
            // --binary -> When this build starts we should provide all the app packages/files
            openshift.newBuild("--name=ib-${environment}-${appName}", "--image-stream=${image}", "--binary", "--to=${appName}", "--labels=build-type=pipeline", "--labels=app=${appName}")
        }
    }
}

def createImageStream(namespace, appName, environment, image, envVars) {
    openshift.withCluster() {
        openshift.withProject("${namespace}") {             
            // --image-stream -> Base container image for the application container image
            // --binary -> When this build starts we should provide all the app packages/files
            openshift.newBuild("--name=ib-${environment}-${appName}", "--image-stream=${image}", "--binary", "--to=${appName}", "--labels=build-type=pipeline", "--labels=app=${appName}", "${envVars}")
        }
    }
}

def createImageStream(namespace, appName, environment, image) {
    createImageStream(namespace, appName, environment, image, "")
}

def startBinary(namespace, appName, environment) {
    startBinary(namespace, appName, environment, ".")
}

def startBinaryFile(namespace, appName, environment, filePath) {
    openshift.withCluster() {
        openshift.withProject("${namespace}") {
            try {                    
                def istag = openshift.selector("istag", "${appName}:latest")

                if (istag.exists()) {
                    istag.delete()
                }

                openshift.selector("bc", "ib-${environment}-${appName}").startBuild("--from-file=${filePath}", "--wait")
            } catch (err) {
                echo "Failed: ${err}"
            }                      
        }
    }
}

def startBinary(namespace, appName, environment, fromDir) {
    openshift.withCluster() {
        openshift.withProject("${namespace}") {
            try { 

              try {  
                def istag = openshift.selector("istag", "${appName}:latest")
                if(istag.exists()) {
                  istag.delete()
                }
              } catch (err) {
                 echo "IS Tag Failed: ${err}"
              }                      

              openshift.selector("bc", "ib-${environment}-${appName}").startBuild("--from-dir=${fromDir}", "--wait")
            } catch (err) {
                echo "Failed: ${err}"
            }                      
        }
    }
}

return this;
