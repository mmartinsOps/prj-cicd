def execute(namespace, appName, appVersion, environment, options) {
    echo "Deploying.... ${appName}-${appVersion} environment ${environment}"
    openshift.withCluster() {
        openshift.withProject("${environment}-${appName}") {
            try {       
              def dcExists;
              echo "Checking if dc/${appName} exists..."
              try {
                def dc = openshift.selector('dc', "${appName}")
                dcExists=dc.exists()
              } catch (err) {
                dcExists=false;      
              }
                
              echo "Checking if dc/${appName} exists... ${dcExists}"
              if(dcExists) {
                echo "Tag ${namespace}/${appName}:${appName}:${appVersion}"                
                openshift.tag("${namespace}/${appName}:latest", "${appName}:${appVersion}")
              } else {
                echo "New app ${namespace}/${appName}:latest" 
                echo "Options: ${options}"
                echo "[oc new-app ${namespace}/${appName}:latest --name=${appName} ${options}]"
                //openshift.newApp("--image-stream=${namespace}/${appName}:latest", "--name=${appName} ${options}") 
                openshift.raw("new-app --image-stream=${namespace}/${appName}:latest --name=${appName} ${options}")
              }
            } catch (err) {
                error("[lib] - Deploy failed: ${err}")
            }    
        }
    }
}

return this;
