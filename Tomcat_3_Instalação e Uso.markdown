#3 Instalação

Antes de tentar instalar o Tomcat, é importante ter um ambiente Java instalado de forma correta.

O Tomcat 8 requer um ambiente JRE 7 instalado. Deve-se configurar a variável de ambiente `$JAVA_HOME` ou `$JRE_HOME` indicando a localização do ambiente.

Dependendo de como foi instalado, as localizações dos arquivos de configuração, scripts e webapps poderão variar.

A instalação também poderá ser diferente para cada versão. O ideal é verificar o arquivo `RUNNING.txt` correspondente à versão usada, que faz parte da distribuição e documentação do Tomcat.

##Instalação como aplicação

Independente de plataforma, o Tomcat pode ser instalado simplesmente baixando o `zip` ou `tar.gz` disponível no site, expandindo o arquivo em um diretório que será o `$CATALINA_HOME`. 

`$CATALINA_HOME` deverá ser registrado como variável de ambiente (e `$CATALINA_HOME/bin` no `$PATH` caso seja desejado que os scripts executem de qualquer lugar).

##Instalação como serviço

A instalação como *serviço* é dependente de plataforma. A instalação como serviço poderá facilitar o uso do servidor, garantindo o reinício automático, e facilidades para interromper, iniciar e reiniciar o serviço.

Instaladores de serviço garantem a instalação das dependências e configuração das variáveis de ambiente, mas a localização dos arquivos poderá ser diferente.

###Serviço Tomcat no Windows
Para instalar um serviço no Windows deve-se baixar o instalador disponível no site tomcat.apache.org. 

###Tomcat 8 no Ubuntu 15

No Ubuntu 15, o Tomcat 8 pode ser instalado como serviço usando um único comando:

`$ sudo apto-get install tomcat8`

A instalação default será em `/etc/tomcat8`.

###Tomcat 8 no Ubuntu 14

No Ubuntu 14, o mesmo pode ser feito para instalar o Tomcat 7. para instalar o Tomcat 8, neste caso, é necessário baixar as depedendências, o pacote binário e configurar as permissões.

No exemplo abaixo instala-se o JDK, cria-se um usuário/grupo `tomcat/tomcat`, baixa-se o pacote Tomcat 8, cria-se a pasta /opt/tomcat e expande-se o pacote nessa pasta:

    $ sudo apt-get install default-jdk
    $ sudo groupadd tomcat
    $ sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
    $ wget http://mirror.nbtelecom.com.br/apache/tomcat/tomcat-8/v8.0.32/bin/apache-tomcat-8.0.32.tar.gz
    $ sudo mkdir /opt/tomcat
    $ sudo tar xvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1
    
Em seguida deve-se configurar as permissões da pasta recursivamente (para que todo o pacote pertenca ao usuário/grupo `tomcat/tomcat`).

###Tomcat no Mac OS

No ambinente deve primeiro estar instalado o pacote *HomeBrew* (`http://brew.sh`). O comando abaixo instala a versão mais recente:

    $ brew install tomcat

##Instalação embutida
A instalação também pode ser realizada através de um ambiente de desenvolvimento (IDE) que criará instância separadas para diferentes aplicações. 

**[Como instalar e configurar Tomcat no Eclipse]**

##Execução
Para iniciar, finalizar e reiniciar o Tomcat instalado como serviço, é preciso utilizar as ferramentas que diferem em cada plataforma. Em Windows use a aplicação gráfica que é fornecida. Em ambientes Maco OS e Linux, geralmente utiliza-se instruções de linha de comando específicas da plataforma.

###Tomcat instalado como aplicação

A maior parte da funcionalidade multiplataforma está no script `catalina` (`catalina.bat` ou `catalina.sh`). Outros scripts são atalhos para funções comuns de `catalina`.

* `catalina` - contém o código que realiza start, stop e restart
* `startup` - roda `catalina start`
* `shutdown` - roda `catalina stop`
* `version` - roda `catalina version`
* `digest` - usado para gerar senhas criptografadas

Há também outros scripts que são exclusivos para a plataforma usada.

O script **catalina** também contém outras opções além de `version`, `start` e `stop`.

* `run` - igual a start, mas não grava logs
embedded - modo embutido, geralmente usado por IDEs
* `debug` - modo debug
* `-help` lista outros comandos

Os comandos também pode ser definidos como propriedades globais da JVM. Neste caso devem ser declarados na variável `CATALINA_OPTS`.

Os scripts ficam em `$CATALINA_HOME/bin`. Se este caminho estiver no `$PATH`, o Tomcat pode ser controlado de qualquer lugar:

Por exemplo, para iniciar:

* `startup.bat` ou `startup.sh`
* `catalina.bat start` ou `catalina.sh start`

e para finalizar o serviço:

* `shutdown.bat` ou `shutdown.sh`
* `catalina.bat stop` ou `catalina.sh stop`

A instalação básica do Tomcat não oferece serviço de reinício nem de início automático. Para reiniciar é preciso interromper o serviço e depois reiniciar (`catalina stop`, seguido de `catalina start`). O shutdown poderá demorar.

Se o Tomcat for instalado como serviço, é possível que o sistema operacionalo oferece a possibilidade de reiniciar o Tomcat.

###Tomcat instalado como serviço

Se Tomcat foi instalado como um serviço, deve-se usar as opções disponíveis na aplicação que gerencia o serviço para iniciar, reiniciar e interromper o processo. Isto depende do sistema operacional usado. O Windows oferece essa opção através de uma interface gráfica. Em linha de comando e sistemas Unix/Linux geralmente usa-se um script `init` ou `service`, por exemplo:

    /etc/rc.d/init.d/tomcat start

ou

    service tomcat start

O nome do serviço poderá ser diferente. Geralmente tem o mesmo nome do serviço instalado (ex: `tomcat7`, `tomcat8`, etc.)

É possível usar ferramentas do sistema operacional para garantir o reinício automático e início ao login do Tomcat mais facilmente se ele estiver instalado como serviço.

##Variáveis de ambiente importantes

Na inicialização, o log por default começa imprimindo as variáveis de ambiente. Se o Tomcat for iniciado como aplicação, o log por default é impresso na tela do console. Verifique as variáveis usadas na inicialização. 

    $ catalina.sh start    Using CATALINA_BASE: /home/helderdarocha/apache-tomcat-8.0.32    Using CATALINA_HOME: /home/helderdarocha/apache-tomcat-8.0.32    Using CATALINA_TMPDIR: /home/helderdarocha/apache-tomcat-8.0.32/temp    Using JRE_HOME: /usr/java/jdk1.8.0_01
Erros ou falha na inicialização poderão ocorrer com `JAVA_HOME` ou `JRE_HOME` definidas incorretamente.##Teste da instalação

Inicie o serviço rodando os scripts em `$CATALINA_HOME/bin` ou usando ferramenta de administração do serviço Tomcat.

Acesse o site `localhost:8080`.##Erros de instalação comuns
Os principais erros de instalação tem a ver com instâncias rodando previamente e conflito de portas. A porta default do Tomcat é 8080, que é uma porta frequentemente usada por default por vários serviços simples que criam servidores Web. Verifique se sua plataforma já não contém outro serviço rodando nessa porta usando as ferramentas específicas do seu sistema operacional (ex: `grep`, `netstat`). Se você estiver usando algum IDE que roda o Tomcat, ele também pode estar usando a porta, causando conflito. 
As portas usadas pelo Tomcat podem ser alteradas com facilidade na configuração.O **shutdown** do Tomcat não é garantido, e pode demorar. Um servlet pode ter criado um thread com prioridade mais alta, que impede o shutdown, pode ter havido um estouro de memória (que impede o acesso à porta de shutdown). 
Se o processo Java do Tomcat não tiver terminado, a porta pode ainda estar sendo bloqueada e será necessário forçar o seu fim usando ferramentas do seu sistema operacional (kill, etc.) ou mesmo, em casos extremos, reiniciar o sistema.