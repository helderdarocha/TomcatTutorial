#6 Tomcat Manager

##Manager
Manager é uma aplicação distribuída pelo Tomcat que oferece uma interface Web para realizar e gerenciar o deployment de aplicações, iniciar, interromper e reiniciar aplicações individuais e obter informações sobre o ambiente e a sua execução. Faz parte de uma instalação default. 

Se não estiver configurada, ou se houver a necessidade de configurar um manager para outro host, pode-se baixar, copiar ou vincular a aplicação (WAR), instalar em uma pasta de webapps e configurar o contexto criando um arquivo manager.xml em $CATALINA_BASE/conf/Catalina/localhost:

    <Context privileged="true" antiResourceLocking="false"
             docBase="${catalina.home}/webapps/manager">
      <Valve className="org.apache.catalina.valves.RemoteAddrValve"
             allow="127\.0\.0\.1" />
    </Context>
    
O Valve acima garante que o manager seja acessível apenas de localhost. 

Apesar de disponível, a instalação default do Tomcat não é distribuída com o acesso ao Manager habilitado. É preciso escolher um ou mais roles de acesso e associá-los a um usuário novo ou existente em CATALINA_BASE/conf/tomcat-users.xml. Os roles disponíveis são:

* manager-gui — Acesso à interface gráfica completa (/manager/html)
* manager-status — Acesso apenas à página "Server Status" (/manager/status)
* manager-script — Acesso à interface de texto (/manager/text) e à página "Server Status" - permite o deploy e outras funcionalidades via comandos de URL
* manager-jmx — Acesso à interface JMX e à página "Server Status" (/manager/jmxproxy)

Exemplo:

    <tomcat-users>        <user name="tomcat" password="tomcat" roles="tomcat" />        <user name="admin"  password="tomcat" roles="manager-gui" />        <user name="monitor"  password="tomcat" roles="manager-jmx, manager-script" />     </tomcat-users>

A interface HTML está localizada (em uma instalação default) em http://localhost:8080/manager/html

As interfaces JMX e Script são vulneráveis a ataques externos, e portanto, devem ser usadas apenas em escopos limitados. Permitem amplo acesso a serviços do Tomcat e dados de monitoração. São destinados, principalmente, a aplicações cliente (IDEs), ferramentas de automação e de monitoração. Para maiores informações sobre as interfaces JMX e Script, consulte a documentação em:

http://tomcat.apache.org/tomcat-8.0-doc/manager-howto.html

##Outras ferramentas
###Ant e Maven
Vários recursos do Manager são acessíveis através de plugins e tarefas do Ant e Maven. Consulte os manuais de Ant e Maven para mais informações.

###IDEs
Várias IDEs utilizam as interfaces de administração do Tomcat internamente.

###TCD
Outra maneira de fazer o deployment de aplicações é usar uma ferramenta externa como o Ant, o Maven, uma IDE ou o TCD (Tomcat Client Deployer) - que deve ser baixado separadamente.