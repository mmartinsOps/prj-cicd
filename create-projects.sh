    #!/bin/sh

    JENKINS_PROJECT_NAME=cicd
#    PROJECT_NAME=testando
    invalid() {
        echo "Error: Invalid parameters!"
        echo
    }

    usage() {
        echo "$ create-build.sh <project-name>"
        echo
        echo "Argumentos:"
        echo
        echo "   project-name:  Nome base do projeto a ser criado / atualizado"
        echo
    }

    if [ "$PROJECT_NAME" == "" ]; then
        invalid
        usage
        exit 1
    fi

    echo "------------------------------------------------------------------------------------"
    echo "- VERIFICANDO PROJETOS                                                             -"
    echo "------------------------------------------------------------------------------------"

    oc get project $PROJECT_NAME > /dev/null

    if [ "$?" == "1" ]; then
        echo "Criando projeto de DEV"
        oc new-project $PROJECT_NAME > /dev/null
        helm repo add sealed-secrets https://bitnami-labs.github.io/sealed-secrets  > /dev/null
        helm repo update  > /dev/null
        helm install --namespace $PROJECT_NAME  sealed-secrets-controller sealed-secrets/sealed-secrets --set secretName=$PROJECT_NAME   > /dev/null

    fi

    echo "------------------------------------------------------------------------------------"
    echo "- ADICIONANDO POLICIES AOS PROJETOS                                                -"
    echo "------------------------------------------------------------------------------------"

    oc adm policy add-role-to-user admin \
        system:serviceaccount:"${JENKINS_PROJECT_NAME}:jenkins" -n $PROJECT_NAME

    echo "------------------------------------------------------------------------------------"
    echo "- ANOTANDO PROJETOS                                                                -"
    echo "------------------------------------------------------------------------------------"

     oc annotate ns $PROJECT_NAME openshift.io/node-selector='region=dev' -o yaml --overwrite > /dev/null
