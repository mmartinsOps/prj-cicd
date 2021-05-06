# DevOps

## Projeto DevOps - Automação de ALM

O diretório ```openshift``` contém *templates* para serem criados/aplicados no *cluster*. <br/><br/>
O diretório ```jenkins-agent-container-images```contém os *Dockerfiles* necessários para criar as imagens de contêineres com o *Jenkins Agent* e as ferramentas necessárias para o *build* da aplicação. <br/><br/>
O diretório ```devops-resources``` contém as bibliotecas em ```Groovy``` com as tarefas mais comuns para os *pipelines* no *Jenkins*. <br/><br/>

O diretório ```environment``` contém informações importantes para os *pipelines*. Análogo ao _projetos_, porém itens para "genéricos" para o *pipeline*, não itens para uma determinada aplicação. <br/><br/>
O diretório ```configuration``` contém informações importantes para os *pipelines*. Análogo ao environment, porém itens para "genéricos" para as *aplicações* em geral, não itens para uma determinada aplicação. <br/><br/>
O diretório ```projetos```contém as todas as informações, específicas para cada projeto de software (aplicação), que o *pipeline* precisa para executar, por exemplo:<br/>
variavéis de ambiente, senhas ou qualquer outra informação importante para que o *build* e o *deploy* (automáticos)da aplicação sejam bem sucedidos.<br/><br/>
Serão considerados, inicialmente, os seguintes sub-diretórios e arquivos:
+  projetos/<NOME DA APLICACAO>/app-env/config.json"              
+  projetos/<NOME DA APLICACAO>/app-env/variables.json"
+  projetos/<NOME DA APLICACAO>/app-env/secrets.json"
+  projetos/<NOME DA APLICACAO>/bc-env/config.json"
+  projetos/<NOME DA APLICACAO>/bc-env/variables.json"
+  projetos/<NOME DA APLICACAO>/bc-env/secrets.json"
+  projetos/<NOME DA APLICACAO>/dc-env/config.json"
+  projetos/<NOME DA APLICACAO>/dc-env/variables.json"
+  projetos/<NOME DA APLICACAO>/dc-env/secrets.json"
+  projetos/<NOME DA APLICACAO>/patches/patches.json"
+  projetos/<NOME DA APLICACAO>/templates/template.json"
+  projetos/<NOME DA APLICACAO>/templates/template.yaml"
+  projetos/<NOME DA APLICACAO>/templates/template.yml"

> **Importante**: Sempre salve os arquivos com as informações e definições dos objetos que serão criados/modificados no *cluster* no repositório de versionamento de arquivos (Git, por exemplo). **Evite**, ou mesmo nunca faça alterações sem que os dados estejam sincronizados com o repositório.

### Como utilizar esses recursos para criar *pipelines* para as aplicações
A plataforma **Openshift** tem muitas opções para que a gestão do ciclo de vida das aplicações e é muito útil para facilitar muitas tarefas, diminuindo o tempo necessário para criar e implantar soluções.

Estes recursos definem uma destas muitas opções, usando o servidor de *CI/CD* *Jenkins* para executar as automações (*pipelines*).
Utilizando a rica integração que o **Openshift** provê com o servidor *Jenkins*, podemos criar *pipelines* para automatizar praticamente qualquer tarefa.

Faz parte destes recursos um *template* para a criação de *pipelines* chamado de *Git Branch Source Pipeline*, este *template* cria um *build pipeline* no **Openshift**, que ao iniciar, lista todas as *branches* do repositório (de versionamento de códigos) indicado e para cada *branch* cria um *build pipeline*, que utilizará um *Jenkinsfile* (*pipeline*) "genérico" para determinada tecnologia ou perfil de aplicação.<br/> 
Para identificar qual é o tipo de tecnologia, fica convencionado que o arquivo com os passos para o *pipeline* (*Jenkinsfile*), terá seu nome definido obedecendo o padrão: ```Jenkinsfile.<tecnologia>```.<br/><br/>
Por exemplo: <br/>
O arquivo ```Jenkinsfile.java.springboot```, define um *pipeline* com todos os mínimos passos necessários para criar uma imagem de cointêiner a partir dos arquivos no repositório de código (*Git*).<br/>
> Foi planejado que os repositórios com os códigos fonte da aplicação e o repositório contendo estes artefatos (devops), são diferentes, portanto o repositório com o código fonte, não deve conter nenhum elemento necessário para o *build* e * deploy*, estes ficaram no repositório que contém estes recursos (devops).

Todos os *Jenkinsfiles* devem informar em qual *Jenkins Agent* ele deve ser executado, aproveitaremos para indicar qual a imagem de contêiner necessária para que o *pipeline* seja executado.<br/>
Esta imagem de contêiner (*Jenkins Agent Container*) contém todas as ferramentas necessárias para a execução do *pipeline* e geralmente usamos como imagem base uma imagem que contém o *Jenkin Agent*, por exemplo: ```quay.io/openshift/origin-jenkins-agent-maven:4.1.0```.
<br/><br/>

> Todas os *Dockerfiles* necessários para a criação destas imagens, devem estar salvos no diretório ```jenkins-agent-container-images```.

O primeiro passo para o uso destes recursos, seria criar o *template* *Git Branch Source Pipeline* no *cluster* **Openshift**.<br/>
Depois de criar o *template*, precisamos importar as imagens base que serão parte da aplicação (imagem de contêiner com a tecnologia e recursos da aplicação).<br/>

> Verifique o arquivo ```README.MD``` no diretório ```openshift/import-images```, para informações de como criar *secrets* para acessar repositórios de imagens de contêiners externos com autenticação.<br/>Aproveite esse diretório para criar *bash scripts* para importar as imagens.

Agora vamos definir os *Dockerfiles* para as *Jenkins Agent Images*.<br/>
A seguir, definir e revisar os *Jenkinsfiles*, para os "modelos" com os passos necessários para o *build* e *deploy* (*pipeline*) das aplicações.<br/><br/>
Finalmente, podemos utilizar o *template* e criar os *pipelines*, indicando qual é a *URL* do repositório de código com a aplicação, credenciais de acesso, *URL* do repositório de código com estes recursos (devops), credenciais de acesso, tipo de "tecnologia" (ou modelo do *pipeline*).<br/><br/>

> Assim que um novo modelo de *build* ou *deploy*, ou qualquer sequencia de passos necessários para que as aplicações (imagens de contêiner com a tecnolgia e informações geradas a partir do código fonte (pacotes, ou arquivos)) possam ser geradas e executadas na plataforma surgir, deve-se planejar para criar e/ou atualizar os *Dockerfiles* e *Jenkinsfiles* neste repositório.<br/><br/>


### Criando *templates* no *cluster* **Openshift**

Utilizando os arquivos do diretório ```openshift```:<br/>

```bash
# oc apply -f <nome do arquivo>
```
ou

```bash
# oc create -f <nome do arquivo>
```
ou na *web console* <br/>
![Add to project - Icone + no topo à direita, "Import YAML"](/docs/assets/images/import-yaml-json-form.png)
copie e cole o conteúdo.
<br/>

### Criando *Jenkins Agent Images*
Usando a ferramenta ```oc```, cliente linha de comando para o **Openshift**.
```bash
# cat <Dockerfile> | oc new-build -D - --name <nome do build>
```

> Nota: o nome do *build* é utilizado como nome padrão para a imagem criada. Define o nome, não a **tag**, sempre que o *build* for executado uma imagem com a *tag* **latest** será gerada. <br/><br/>

Exemplo:
```bash
# cat Dockerfile.java-8 | oc new-build -D - --name jenkins-agent-java-8
```

### Utilizando *Jenkins Agent Images* nos *pipelines*
Quando criamos os arquivos *Jenkinsfile* precisamos definir qual é o *agent* que executará o *pipeline*, nessa configuração definiremos qual imagem de contêiner deve ser utilizada como *Jenkins Agent*.
A plataforma **Openshift** tem duas imagens de contêiner (*Jenkins Agent Images*) pré carregadas com configurações pré definidas no *Jenkins*:
+ maven
+ node

Utilizaremos uma dessas como exemplo de configuração de *agent* para definirmos que o *pipeline* deve ser executado na imagem de contêiner que indicarmos, por exemplo:

```groovy
  agent {
    kubernetes {
      label "jenkins-agent-java-8"
      cloud "openshift"
      inheritFrom "maven"
      containerTemplate {
        name "jnlp"
        image "docker-registry.default.svc:5000/devops/jenkins-agent-java-8:latest"
      }
    }
```

### Criando um *pipeline* com o *template* ***Git Branch Source Pipeline***
Na *web console* do **Openshift**, no menu lateral (à esquerda), na visão *"Developer"*, clique na opção *"+Add"* e então em *"From Catalog"*.<br/>
![Catalog - menu lateral](/docs/assets/images/left-menu-catalog-item.png)
Em seguida, use o componente de filtro para filtrar os itens do catálogo pelo nome do template.<br/>
Neste caso: **branch**<br/>
O ícone do *template* aparecerá na tela.<br/>
![Filtro dos itens do catálogo](/docs/assets/images/main-frame-filter-result.png)
Clique no ícone para abrir o formulário de criação do *pipeline*.<br/>
![Filtro dos itens do catálogo](/docs/assets/images/pipeline-template-form-info.png)
Preencha os valores, no formulário, como *URL* do repositório de código com a aplicação,  *URL* do repositório de código os artefatos descritos neste documento (devops), credenciais para acesso aos repositórios e o tipo de 'tecnologia' (modelo de *pipeline*. Ex.: java.springboot), para que o *build pipeline template* crie os *build pipelines* para as *branches* encontradas no repositório de código da aplicação, utilizando o *pipeline* correto (Ex.: *Jenkinsfile.java.springboot*).
![Formulário do *template*](/docs/assets/images/pipeline-template-form-config.png)
Exemplo:
+ **Nome da aplicação**: petshop
+ **Nome da imagem base**: redhat-openjdk18-openshift
+ **Branch do repositório git**: master
+ **Nome da secret do repositório git**: devops-git
+ **URL do repositório git**: http://gogs-cicd.apps.cluster-4117.4117.sandbox235.opentlc.com/PRODESP/petshop.git
+ **Linguagem do código fonte**: java.springboot
+ **Devops Git URL**: http://gogs-cicd.apps.cluster-4117.4117.sandbox235.opentlc.com/PRODESP/devops.git
+ **Devops Git Branch**: master
+ **Devops Source Secret**: devops-git
Depois da confirmação da criação do *build pipeline* *branch-source*, podemos ver o objeto criado no *cluster* **Openshift**.
![Menu esquerdo *builds*](/docs/assets/images/left-menu-builds-pipelines-item.png)
Ao clicar no botão *Start Build* este *pipeline* vai detectar as *branches* do repositório de código informado no campo **URL do repositório git** (do formulário do *template*, Ex.: http://gogs-cicd.apps.cluster-4117.4117.sandbox235.opentlc.com/PRODESP/petshop.git).
![Botão de inicio de execução dos *builds pipelines*](/docs/assets/images/build-pipelines-branch-source-start-button.png)
Para cada *branch* encontrada será criado um novo *build pipeline* preparado para executar o *pipeline* configurado no formulário do *template*, ex.: java.springboot (que vai executar o *Jenkinsfile.java.springboot*).
![*Builds pipelines* das *branches* do repositório informado](/docs/assets/images/build-pipelines-main-master-branch.png)
Analogamente, ao clicar no botão *Start Build* (agora ao lado do *build pipeline* da *branch* *master* do repositório) o *pipeline* será executado, criando todos os objetos necessários para a aplicação na plataforma **Openshift**.
O *status* da execução pode ser acompanhado pela interface *web console*, na mesma página que lista os *Builds*
![Menu esquerdo *builds*](/docs/assets/images/left-menu-builds-pipelines.png)
![*Builds* status](/docs/assets/images/builds-pipelines-main-pipeline-status.png)

Os objetos gerados pelos *pipelines* aqui definidos criam (pelo menos) esses recursos:
+ project (namespace) - com o nome da aplicação e seus ambientes virtuais (dev, hml, prd)
+ image stream - com o nome da aplicação
+ builder (image builder) - com o nome da aplicação e modo binário
+ config (configMaps e secrets) - com os nomes indicados nos arquivos, dentro do diretório do projeto no repositório de código com os recusos ***devops*** (repositório com estes artefatos)
+ deployment (deploymentConfig) - com o nome da aplicação
+ service - com o nome da aplicação e expondo a porta exposta pela aplicação (contêiner)
+ route - com o nome da aplicação e expondo a porta 80 ou 443 (SSL) para que o acesso de clientes externos ao *cluster* possam acessar a aplicação

> O *pipeline* pode aplicar/criar *templates* (YAML/JSON) no *cluster*, desde que eles estejam definidos como arquivos dentro do diretório *projetos* no repositório de código com os recursos ***devops***.


