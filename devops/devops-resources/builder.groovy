def getBranches (sourceRepoUrl, sourceRepoCredentialsId, appName) {
    // Clear workspace
    deleteDir()
    // Checkout repo
    git url: sourceRepoUrl, credentialsId: sourceRepoCredentialsId
    // get names
    def branchNames = sh(returnStdout: true, script: "git branch -r | grep -v HEAD | awk '{print \$1}' ORS='\n' | cut --complement -d '/' -f 1 | awk '{print \$1}' ORS=','" ).trim()
    echo "Branches: [${branchNames}]"
    def branchesFinal = ""
    branchNames = branchNames.split(",");
    branchNames.each { item ->
        branchesFinal = "${appName}-${item},${branchesFinal}"
    }
    return branchesFinal.split(",");
}

def getBuilds (namespace){
    openshift.withCluster() {
        openshift.withProject("${namespace}") {
            return openshift.selector("bc").names()
        }
    }
}

def createBuilds (branchList, appName, appCode, namespace) {

    openshift.withCluster() {
        openshift.withProject("${namespace}") {

            branchList.each { item ->

                def masterPipelineBC = openshift.selector("bc", "${appName}-branch-source")
                def masterPipelineBCObj = masterPipelineBC.object()
                def masterPipelineBCName = masterPipelineBCObj.metadata.name
                def branchName = "${item}"
                branchName = branchName.replaceAll("/","-").toLowerCase()
                masterPipelineBCObj.metadata.name = "${branchName}"

                def index = 0
                for (envEntry in masterPipelineBCObj.spec.strategy.jenkinsPipelineStrategy.env) {
                    if(envEntry.name.equalsIgnoreCase("refs")) {
                        break
                    } else {
                        index++
                    }
                }
                def simpleBranch = item.replaceAll("${appName}-", "")
                masterPipelineBCObj.spec.strategy.jenkinsPipelineStrategy.env[index]["value"] = "${simpleBranch}" as String
                masterPipelineBCObj.spec.strategy.jenkinsPipelineStrategy.jenkinsfilePath = "Jenkinsfile.${appCode}" as String

                if(!openshift.selector('bc', "${masterPipelineBCObj.metadata.name}").exists()) {
                    openshift.create(masterPipelineBCObj)
                    openshift.selector('bc', "${masterPipelineBCObj.metadata.name}").label([branch: "${simpleBranch}"], "--overwrite")
                }    
            }
        }
    }
}

def delete (buildName, namespace) {
    openshift.withCluster() {
        openshift.withProject("${namespace}") {
            def oldBranchPipelineBC = openshift.selector("bc", "${buildName}")
            oldBranchPipelineBC.delete()
            echo "Deleted build-config: ${buildName}"
        }
    }
}

return this;
