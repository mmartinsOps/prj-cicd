def printProject() {
  openshift.withCluster() {
    openshift.withProject() {
      echo "Using project: ${openshift.project()}"
      return "${openshift.project()}"
    }
  }
}

return this;
