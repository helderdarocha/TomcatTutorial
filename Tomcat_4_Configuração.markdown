#4 Configuração

A maior parte da configuração do Tomcat é realizada em arquivos de $CATALINA_HOME/conf. Outras envolvem alterações em scripts e componentes.

Esta seção explorará algumas configurações básicas. Outras configurações serão tratadas em seções a parte.

Os principais arquivos de configuração são:

* `server.xml` - principal arquivo de configuração.
* `web.xml` - arquivo de configuração global para definir defaults para todas as aplicações Web (contém os mesmos elementos do web.xml contido em cada aplicação).
* `tomcat-users.xml` - lista default de roles, usuários e senhas usadas pelo domínio UserDatabaseRealm para autenticação em serviços do Tomcat.
* `catalina.policy` - políticas de segurança Java
* `context.xml` - configuração default para todos os contextos dos hosts da instalação do Tomcat

Após alterar qualquer arquivo de configuração, as alterações só entram em vigor com a reinicialização do servidor.

##server.xml

Cada elemento filho do server.xml representa um objeto de configuração dentro da arquitetura do Tomcat. O elemento raiz é `<Server>` e representa todo o servidor. Um `<Server>` deve conter pelo menos um elemento `<Service>`, que contem um ou mais `<Connector>` (cada um em uma porta diferente) associado a um processador `<Engine>`, que contém um elemento `<Host>`. Cada um desses elementos pode ser configurado por sub-elementos e atributos, definindo portas, classes, timeouts, comportamentos, etc.

Além de elementos `<Service>`, um `<Server>` tipicamente contém um ou mais `<Listener>` (que configura componentes que irão processar eventos do ciclo de vida do servidor) e um `<GlobalNamingResources>` que guarda um ou mais resources JNDI.

A seguir um documento `server.xml` típico (javacodegeeks.com):

	<?xml version='1.0' encoding='utf-8'?>
	<Server port="8005" shutdown="SHUTDOWN">
	  <Listener className="org.apache.catalina.core.JasperListener" />
	  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
	  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
	  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
	  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
 
	  <GlobalNamingResources>
		<Resource name="UserDatabase" auth="Container"
				  type="org.apache.catalina.UserDatabase"
				  description="User database that can be updated and saved"
				  factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
				  pathname="conf/tomcat-users.xml" />
	  </GlobalNamingResources>
 
	  <Service name="Catalina">
		<Connector port="8080" protocol="HTTP/1.1"
				   connectionTimeout="20000"
				   redirectPort="8443" />
		<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
 
		<Engine name="Catalina" defaultHost="localhost">
 
		  <Realm className="org.apache.catalina.realm.LockOutRealm">
			<Realm className="org.apache.catalina.realm.UserDatabaseRealm"
				   resourceName="UserDatabase"/>
		  </Realm>
 
		  <Host name="localhost"  appBase="webapps"
				unpackWARs="true" autoDeploy="true">
			<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
				   prefix="localhost_access_log." suffix=".txt"
				   pattern="%h %l %u %t "%r" %s %b" />
		      <Context path="" docBase="ROOT"/>                 <Context path="/teste" docBase="/home/helderdarocha/teste"                                reloadable="true" crossContext="true"/>
		  </Host>
		</Engine>
	  </Service>
	</Server>

Abaixo uma breve descrição desses principais elementos e atributos.

Elemento raiz:

* `<Server>` - raiz do documento. O atributo `port` informa a porta para onde é enviado o comando de shutdown do servidor. Se o valor for -1, a porta será desabilitada. O atributo `address` informa o IP ou nome da máquina que pode enviar comando de shutdown. Se ausente a máquina será localhost. O atributo `shutdown` informa a string de comando que deve ser recebida na porta informada para dar shutdown no servidor.

Elementos filho:

* `<Listener>` - um ou mais desses elementos informam classes que são notificadas em eventos específicos do ciclo de vida do servidor.
* `<GlobalNamingResources>` - define resources acessíveis via JNDI e podem ser recuperados por nome (atributo `name`).
* `<Service>` - cada um contém uma coleção de conectores (`<Connector>`)associados a um processador (`<Engine>`). Pode haver vários, cada um identificado com um nome (atributo `name`) diferente. Pode também haver um atributo `className` contendo o nome de uma classe que implementa a interface `org.apache.catalina.Service`. 

Sub-elementos de `<Service>`

* `<Executor>` - um pool de threads compartilhado que será compartilhado por todos os conectores. Permite definir um número mínimo (`minSpareThreads`) e máximo (`maxThreads`) de threads ativos e outros atributos de threads.
* `<Connector>` - associado com uma porta TCP/IP (atributo `port`) para realizar interface com o cliente; pode ser um servidor web, um conector AJP ou outro. Pode haver mais de um. Pode conter zero ou mais `<Valve>` (filtros de processamento). Atributos estabelecem timeout (`connectionTimeout`), redirecionamento (`redirectPort`), limite de clientes, threads, executor, protocolo usado e muitas outras propriedades.
* `<Engine>` - representa o processador que recebe todas as requisições de um dos conectores do serviço. Cada processador pode ter seu próprio diretório no sistema (`CATALINA_BASE`) Atributos permitem configurar threads, delays, rotas, etc. Além de `<Host>` pode conter `<Realm>` (domínio de segurança), `<Valve>` (filtros), `<Listener>` e `<Cluster>`.

Outros elementos:

* `<Host>` - um endereço virtual, declarado dentro de um `<Engine>` e pode conter realms, contextos, válvulas, clusters, etc.
* `Context` - representa um contexto ou aplicação Web. Contextos também podem ser criados dinamicamente de várias formas.
* `<Realm>` - usado em `<Engine>` ou `<Host>`, determina um domínio de segurança (banco de dados de usuários e grupos)
* `<Cluster>` - configura clustering (replicação de sessão e atributos de contexto e deployment distribuído)
* `<Valve>` - interceptador de requisições HTTP baseado em padrão (atributo `pattern`). O atributo `className` informa o nome da classe que implementa o Valve, o atributo `prefix` informa o nome do arquivo de log e `directory` onde o arquivo de logs será armazenado.

Pode-se ter vários arquivos `server.xml` com configurações distintas, usando um nome diferente (tipicamente `server-nome.xml`). Para executá-los é preciso informar o nome de cada arquivo ao iniciar o Tomcat:

    $ catalina.sh start -config /conf/server-dois.xml

##web.xml

Cada aplicação instalada em $CATALINA_HOME/webapps possui um arquivo WEB-INF/web.xml dentro de seu contexto. O arquivo web.xml localizado em $CATALINA_HOME/conf é idêntico, mas define defaults que podem ser sobrepostos em qualquer aplicação. Este arquivo é lido antes do web.xml de cada aplicação, de forma que qualquer configuração que for definido nele valerá para todas as aplicações. Isto inclui definições de listeners, variáveis, filtros e até servlets.

##tomcat-users.xml

Este arquivo contém uma lista de usuários, grupos e senhas usadas para acessar aplicações através do domínio UserDatabaseRealm.

    <?xml version='1.0' encoding='utf-8'?>    <tomcat-users>          <role rolename="tomcat"/>          <role rolename="role1"/>          <user username="tomcat" password="tomcat" roles="tomcat"/>          <user username="both" password="tomcat" roles="tomcat,role1"/>          <user username="role1" password="tomcat" roles="role1"/>    </tomcat-users>

##catalina.policy

Um arquivo de policy determina permissões para um determinado escopo. O arquivo catalina.policy permite estabelecer restrições de uso e acesso para o Tomcat quando ele for iniciado com a opção `-security`. O escopo consiste de um conjunto de classes em Java e cada permissão estabelece o nível de acesso permitido.

##catalina.properties

Este arquivo permite definir propriedades que serão reusadas no arquivo server.xml. Por exemplo, pode-se definir uma propriedade:

    versao=3.55

e acessar em server.xml usando a notação `${...}`:

    <Valve version="${versao}" ...>



##Configuração de contexto

Um contexto representa uma aplicação Web padrão, que é um arquivo WAR ou uma pasta contendo WEB-INF/web.xml de acordo com a especificação Java EE para aplicações Web.

O contexto é um caminho virtual para a aplicação. É acessível via cliente Web (ex: http://localhost:8080/contexto)

Há três maneiras de configurar um contexto (aplicação Web) no Tomcat:

1) Declarando em server.xml (dentro de `<Host>`); o caminho, que é o identificador do contexto, é declarado no atributo `path`. O atributo `docBase` informa onde está a aplicação. Pode ser um caminho absoluto ou relativo à pasta appBase do Host:

     <Host name="localhost" appBase="webapps">        <Context path="" docBase="ROOT"/>        <Context path="/extra" docBase="/opt/docs/extra"/>    </Host>

2) Declarado em um arquivo (fragmento) de contexto e configurando a pasta do contexto. Neste caso o `path` é o nome do arquivo menos a extensão `.xml`. O arquivo contém apenas um elemento `<Context>` e sub elementos, e deve estar localizado em $CATALINA_HOME/conf/nome-do-engine/nome-do-host. Por exemplo, o contexto /admin é definido em $CATALINA_HOME/conf/Catalina/localhost/admin.xml, é acessível via http://localhost:8080/admin e contém:
     <Context docBase="${catalina.home}/server/webapps/admin" ...>     </Context>

3) Declarado em um componente WAR (que contém a pasta do contexo) depositado uma pasta de hot-deployment (o `<Host>` deve habilitar). Em uma instalação default, esta pasta é `webapps`. Neste caso o `path` é o nome do arquivo menos a extensão `.war` e a pasta é criada automaticamente.

O arquivo `context.xml` de `conf/` permite definir propriedades globais para todos os contextos.

##Pastas de configuração de processadores
Pastas dentro de `conf/` que têm o nome de processadores (engines), por exemplo `Catalina` contém configuração para contextos. Dentro da pasta de configuração pode haver uma ou mais pastas com os Hosts do processador, dentro dos quais são configurados os componentes de cada host.


##Algumas configurações típicas

Configurações mais complexas (load balancing, clustering, etc.) serão exploradas em seções a parte.

###Como criar instâncias diferentes

Crie uma pasta base para instâncias do Tomcat (CATALINA-BASE) onde a nova pasta será armazenada:
    
    mkdir /opt/tomcat-instances/
    cd tomcat-instances/
    
Depois crie uma pasta para a instância:

    mkdir site1
    cd site1
    
Copie a configuração do Tomcat para esta pasta, e crie versões vazias dos outros diretórios:

    cp -a $CATALINA_HOME/conf .    mkdir common logs temp server shared webapps work
Em server.xml altere a porta de Shutdown e as portas de quaisquer conectores, de forma que tenham valores diferentes da instância principal:
    <Server port="8007" shutdown="SHUTDOWN">
        ...
          <Connector port="8081" .... redirectPort="8444" />
          
Para iniciar essa instância, define CATALINA_BASE com o caminho do diretório criado, e inicie o Tomcat normalmente:

    $ set CATALINA_BASE="/opt/tomcat-instances/site1"    $ set CATALINA_HOME="/opt/tomcat"    $ export CATALINA_BASE CATALINA_HOME    $ service tomcat start  
Ou crie um script próprio para essa inicialização.

###Como trocar porta de serviço

A porta default de serviço para o Tomcat é 8080. Para trocar por outra porta, localize o Conector principal no arquivo server.xml:

    <Connector port="8080" protocol="HTTP/1.1"                       connectionTimeout="20000"                       redirectPort="8443" />
Mude a porta para o valor desejado e reinicie o servidor.                       

###Como alterar configuração da JVM

Configurações da JVM podem ser alteradas através de opções da JVM passada através da variável de ambiente JAVA_OPTS, que pode ser declarada no script de execução ou via linha de comando.

Algumas configurações típicas incluem alteração do tamanho do heap, da área de memória usada para classes (PermGen), propriedades usadas para gerenciamento via JMX (jconsole), etc.

A linha abaixo declara algumas propriedades da JVM para o Tomcat:

    JAVA_OPTS="-Xdebug -Xms384M -Xmx384M -Dfile.encoding=UTF-8"

###Como configurar hosts virtuais

Hosts virtuais podem ser configurados em Tomcat adicionando elementos `<Host>` adicionais associando o nome do host a um diretório diferente para webapps. Primeiro, é preciso ter o host virtual já configurado no servidor de nomes (ou estaticamente, para teste ou uso local), e apontando para o endereço onde está rodando o Tomcat. Por exemplo:

    <Engine name="Catalina" defaultHost="localhost">          <Host name="localhost" appBase="webapps">               ...          </Host>          <Host name="www.samovar.com.br" appBase="/opt/hosts/samovar/webapps">               <Alias>samovar.com.br</Alias>               <Context path="" docBase="ROOT"/>          </Host>    </Engine>

Alias pode ser usado para adicionar aliases para o host.

## Mais informações
http://tomcat.apache.org/tomcat-8.0-doc/config/

