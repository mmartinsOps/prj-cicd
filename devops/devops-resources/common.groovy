def getMvnCmd() {
   def result = "mvn -s configuration/maven/settings.xml"
   return result
}

def appBranchNormalizer(branch) {
   appBranch = branch.replaceAll("[^a-zA-Z0-9]+", "-")
   return appBranch.toLowerCase()
}

def pomVersion() {
  def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
  return matcher ? matcher[0][1] : null
}    

def loadAppEnv(appName) {
   if (fileExists("projetos/${appName}/app-env/variables.json")) {
      def var = readJSON file "projects/${appName}/app-env/variables.json"
      return var
   } else {
      return ""
   }
}

def loadAppSecret(appName) {
   if (fileExists("projetos/${appName}/app-env/secrets.json")) {
      def var =  readJSON file "projects/${appName}/app-env/secrets.json"
      return var
   } else {
      return ""
   }
}

def loadDCEnv(appName) {
   if (fileExists("projetos/${appName}/dc-env/variables.json")) {
      def var =  readJSON file "projects/${appName}/dc-env/variables.json"
      return var
   } else {
      return ""
   }
}

def loadDCSecret(appName) {
   if (fileExists("projetos/${appName}/dc-env/secrets.json")) {
      def var =  readJSON file "projects/${appName}/dc-env/secrets.json"
   } else {
      return ""
   }
}

def createProject(namespace, appName, environment) {
   openshift.withCluster() {
      try {
          def project;
        
          try {
            project = openshift.selector("project ${environment}-${appName}");
            echo "Project found: [${project}]"            
          } catch(err) {
            echo "Project ${environment}-${appName} not found: ${err}"
          }
                    
          try {
            while(project) {
              try {
                project = openshift.selector("project ${environment}-${appName}");
	              echo "Result: [${result}]";                           
              } catch(err) {
                project = null;
              }
            }
            
            def result = openshift.newProject("${environment}-${appName}")
            echo "Result: [${result}]";           
            echo "${environment}-${appName} project created!"
          } catch (err) {
            echo "Create new-project failed: [${err}]"
          }
        
          try {          
            //oc policy add-role-to-group system:image-puller system:serviceaccounts:#appName -n #cicd
            openshift.policy("add-role-to-group", "system:image-puller", "system:serviceaccounts:${environment}-${appName}", "--namespace=${namespace}")
            echo "image-puller role applied to default!"
          } catch (err) {
            echo "Apply imager-puller role failed: [${err}]"            
          }
        
          try {                      
            //oc -n $appname policy add-role-to-user edit system:serviceaccount:#devops:jenkins
            openshift.policy("add-role-to-user", "edit", "system:serviceaccount:${namespace}:jenkins", "--namespace=${environment}-${appName}")
            echo "edit role applied to jenkins!"
          } catch (err) {
            echo "Apply edit role failed: [${err}]"            
          }
        
      } catch (err) {
            echo "Failed: ${err}"            
      } 

      return "${environment}-${appName}";
   }
}

def exposeService(appName, environment) {
   exposeService(appName, environment, "")
}

def exposeService(appName, environment, context) {
   openshift.withCluster() {
      openshift.withProject("${environment}-${appName}") {
         try {
           def appOldRoute;
           try {
             appOldRoute=openshift.selector("route", "${appName}")

             if (appOldRoute.exists()) {
               appOldRoute.delete()
             }
           }catch(err) {
             echo "Find old route: ${err}"
           }
           def appServiceExists;
           try {
             def appService = openshift.selector("svc", "${appName}")
             appServiceExists = appService.exists();
           }catch(err) {
            appServiceExists = false;
           }
           
           if(appServiceExists) {
               def resultRoute = appService.expose("--path=/${context}")
               def appRoute;
             try {
               appRoute = openshift.selector("route", "${appName}").object()
			 }catch(err) {
               echo "Find new route: ${err}"
             }  
               echo "HTTPS: ${appRoute.spec.tls}"
               def scheme = "https://"
               if (appRoute.spec.tls == null) {
                  scheme = "http://"
               } 
               appURL = "${scheme}${appRoute.spec.host}"
               echo "Application URL: ${appURL}"
           }
         } catch (err) {
            echo "Failed: ${err}"
         }
      }
   }
}

def discoveryFirsEnvironment(branch) {
   switch(branch) { 
      case "develop": 
         return "dev"
         break
      case "release":
         return "hml"
         break;
      default:
         return "dev"
   }
}

return this;
