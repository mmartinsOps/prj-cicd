    #!/bin/sh

    invalid() {
        echo "Error: Invalid parameters!"
        echo
    }

    usage() {
        echo "$ create-argo.sh <CD>"
        echo
        echo "Argumentos:"
        echo
        echo "   CD:  Nome base do projeto a ser criado / atualizado"
        echo
    }

    if [ "$CD" == "" ]; then
        invalid
        usage
        exit 1
    fi

    echo "------------------------------------------------------------------------------------"
    echo "- VERIFICANDO PROJETOS                                                             -"
    echo "------------------------------------------------------------------------------------"

    oc get project $CD > /dev/null

    if [ "$?" == "1" ]; then
        echo "Criando namespace"
#        oc create namespace $CD
        oc new-project $CD > /dev/null
        oc -n $CD apply -f https://raw.githubusercontent.com/argoproj/argo-cd/v1.2.2/manifests/install.yaml
        ARGOCD_SERVER_PASSWORD=$(oc -n $CD get pod -l "app.kubernetes.io/name=argocd-server" -o jsonpath='{.items[*].metadata.name}')
        
        echo "Criando Rotas"

        PATCH='{"spec":{"template":{"spec":{"$setElementOrder/containers":[{"name":"argocd-server"}],"containers":[{"command":["argocd-server","--insecure","--staticassets","/shared/app"],"name":"argocd-server"}]}}}}' > /dev/null
        oc -n $CD patch deployment argocd-server -p $PATCH > /dev/null
        oc -n $CD create route edge argocd-server --service=argocd-server --port=http --insecure-policy=Redirect > /dev/null
        echo $ARGOCD_SERVER_PASSWORD
    fi