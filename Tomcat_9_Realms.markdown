#9 Realms 

Não existe (ainda) na especificação Servlet nem na especificação Java EE uma API portátil e unificada para segurança. As APIs de segurança do Java EE apenas tratam de atribuição de roles (autorização) e oferecem uma interface para serviços nativos de autenticação.

Realms (base de dados com informações de usuários e roles/grupos) não fazem parte das especificações do Java EE. O Tomcat oferece uma interface Realm que pode ser implementada por plugins para diferentes implementações dessa base de dados. O nome do Realm é uma classe do pacote `org.apache.catalina.*`:

* `JDBCRealm` - dados de autenticação em banco de dados, acessível via driver JDBC
* `DataSourceRealm` - dados de autenticação em banco de dados, acessível via JNDI JDBC DataSource.
* `JNDIRealm` - dados de autenticação em servidor LDAP acessado via JNDI.
* `UserDatabaseRealm` - dados de autenticação em resource JNDI UserDatabase (tipicamente conf/tomcat-users.xml).
* `MemoryRealm` - dados de autenticação na memória, inicializado a partir de arquivo XML (conf/tomcat-users.xml).
* `JAASRealm` - dados de autenticação via JAAS.

##Configuração de um Realm

Elemento `<Realm>` pode ser usado dentro de um `<Engine>`, `<Host>` ou `<Context>`. O escopo do Realm será limitado pelo escopo do elemento (se definido no Engine, valerá para todos os Hosts, a menos que seja redefinido localmente no Host, e assim por diante.)

A configuração requer pelo menos o nome da classe, e atributos opcionais, dependendo do tipo de Realm usado:

    <Realm className="org.apache.catalina.NOME-DO-REALM" .../>
   
Por exemplo, o JDBCRealm requer a existência de uma tabela de usuários e outra de roles, e atributos com driver JDBC, URL JDBC, dados de autenticação, nome da tabela de usuários, nome da tabela de roles, nome das colunas, etc.

##Exemplo DataSourceRealm

DB:

    DROP DATABASE IF EXISTS tomcat_realm;
    CREATE DATABASE tomcat_realm;
    USE tomcat_realm;
    
    CREATE TABLE tomcat_users (
        user_name varchar(20) NOT NULL PRIMARY KEY,
        password varchar(250) NOT NULL
    );
    CREATE TABLE tomcat_roles (
        user_name varchar(20) NOT NULL,
        role_name varchar(20) NOT NULL,
        PRIMARY KEY (user_name, role_name) 
    );
    
    INSERT INTO tomcat_users (user_name, password) VALUES  ('someuser', '5f4dcc3b5aa765d61d8327deb882cf99');
    INSERT INTO tomcat_roles (user_name, role_name) VALUES ('someuser', 'authenticated');
    COMMIT;

conf/server.xml (dentro de `<Context>` ou elemento mais genérico):

    <Realm className="org.apache.catalina.realm.DataSourceRealm"
       dataSourceName="jdbc/auth"
       digest="MD5"
       userTable="tomcat_users" userNameCol="user_name"     userCredCol="password"
       userRoleTable="tomcat_roles" roleNameCol="role_name"/>
       
META-INF/context.xml (dentro de `<Context>`):

    <Resource name="jdbc/auth" auth="Container" type="javax.sql.DataSource"
       maxActive="100" maxIdle="30" maxWait="10000"
       username="realm_access" password="password"    driverClassName="com.mysql.jdbc.Driver"
       url="jdbc:mysql://localhost:3306/tomcat_realm"/>

##Gerando uma senha
Em vez de guardar uma senha não-criptografada, pode-se usar a ferramenta `digest` e gerar um hash da senha. O comando:

    $ digest.sh -a MD5 senha
    
Retorna um hash MD5 da palavra "senha". Armazene o hash no banco (em vez da palavra não-criptografada) e configure o realm com o atributo `digest="MD5"`, assim quando a senha for digitada ela será convertida em MD5 antes de ser enviada.

Outras opções disponíveis de hash são SHA e MD2.

##Usando um Realm

Uma vez configurado o Realm, ele pode ser usado para configurar autenticação de maneira declarativa no web.xml, ou de maneira programática, usando métodos `login()` ou `authenticate()` da API Servlet.

##Exemplo de uso de um Realm

O web.xml abaixo está configurado para usar autenticação BASIC e a base de dados de autenticação como realm. O Realm usado será o que foi declarado para o Host/Context:

     <web-app ...>
	...
		<security-constraint>
			<web-resource-collection>
				<web-resource-name>Digite someuser/password</web-resource-name>
				<url-pattern>/*</url-pattern>
				<http-method>GET</http-method>
				<http-method>POST</http-method>
				<http-method>DELETE</http-method>
				<http-method>PUT</http-method>
			</web-resource-collection>
			<auth-constraint>
				<role-name>authenticated</role-name>
			</auth-constraint>
		</security-constraint>  
 
		<security-role>
			<role-name>authenticated</role-name>
		</security-role>   
 
		<login-config>
			<auth-method>BASIC</auth-method>
			<realm-name>Fazer login em Tomcat Realm</realm-name>
		</login-config>
	</web-app>
	
A tentativa de acessar o contexto irá enviar para o cliente um cabeçalho HTTP Authenticate, que levará o browser a verificar se possui as credenciais de autenticação para o role `authenticated`. Se não tiver, ele mostrará uma janela onde o usuário poderá digitar os dados.

##Outros exemplos de autenticação Java EE

Veja exemplos adicionais de autorização e autenticação.