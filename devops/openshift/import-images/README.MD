# Configurando credenciais no **OpenShift** para baixar imagens de repositórios externos com autenticação

Criando a *secret*:
```bash
oc create secret docker-registry <nome da secret> \
    --docker-server=registry.redhat.io \
    --docker-username=<conta da red hat - pode ser um e-mail> \
    --docker-password=<senha conta da red hat> \
    --docker-email=<email do responsavel pela conta>
```
<br/>
Exemplo:

```bash
oc create secret docker-registry registry-redhat-io \
    --docker-server=registry.redhat.io \
    --docker-username=user1@redhat.com \
    --docker-password=p4ssw0rd#123 \
    --docker-email=user1@redhat.com
```
<br/>

Associando a *secret* ao perfil *default*:

```bash
oc secrets link default <nome da secret> --for=pull
````
<br/>
Exemplo:

```bash
oc secrets link default registry-redhat-io --for=pull
````
<br/>

Associando a *secreet* ao perfil *builder*:

```bash
oc secrets link builder <nome da secret>
````
<br/>
Exemplo:

```bash
oc secrets link builder registry-redhat-io
````

