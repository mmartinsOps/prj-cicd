def checkoutSCM() {
    echo "Pulling... ${BRANCH_NAME}"
    checkout scm
}

def checkoutGit(branch, namespace, sourceRepoUrl, sourceRepoCredentialsId) {
    echo "Pulling... ${BRANCH_NAME}"
    git branch: "${branch}",
        credentialsId: "${namespace}-${sourceRepoCredentialsId}",
        url: "${sourceRepoUrl}"
}

return this;
