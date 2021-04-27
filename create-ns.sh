    #!/bin/sh

#    CICD=cicd
    
    invalid() {
        echo "Error: Invalid parameters!"
        echo
    }

    usage() {
        echo "$ create-ns.sh <CICD>"
        echo
        echo "Argumentos:"
        echo
        echo "   CICD:  Nome base do projeto a ser criado / atualizado"
        echo
    }

    if [ "$CICD" == "" ]; then
        invalid
        usage
        exit 1
    fi

    echo "------------------------------------------------------------------------------------"
    echo "- VERIFICANDO PROJETOS                                                             -"
    echo "------------------------------------------------------------------------------------"

    oc get project $CICD > /dev/null

    if [ "$?" == "1" ]; then
        echo "Criando namespace"
        oc new-project $CICD > /dev/null
    fi

    oc get dc/jenkins > /dev/null

    if [ "$?" == "1" ]; then
        echo "Criando DC/Jenkins"
        oc process -n openshift jenkins-persistent -p MEMORY_LIMIT=2048M | oc apply -f- -n $CICD > /dev/null
    fi

    oc get dc/sonarqube > /dev/null

    if [ "$?" == "1" ]; then
        echo "Criando DC/Sonarqube"
        oc process -f https://raw.githubusercontent.com/siamaksade/sonarqube/8/sonarqube-persistent-template.yaml | oc create -f- -n $CICD > /dev/null
    fi