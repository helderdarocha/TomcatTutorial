#9 Configuração de datasources

Há duas formas nativas de configurar datasources em Tomcat. Pode-se também usar pacotes externos (ex: C3PO).

## DBCP
A implementação default para pool de conexões JDBC do Tomcat depende de bibliotecas do projeto Apache Commons:

* Commons DBCP
* Commons Pool

A configuração de um Datasource é realizado de forma transparente através de um `<Resource>` criado para um `<Context>`. O exemplo abaixo ilustra a configuração de um DataSource para um banco MySQL:

	<Context>

		<!-- maxTotal: Maximum number of database connections in pool. Make sure you
			 configure your mysqld max_connections large enough to handle
			 all of your db connections. Set to -1 for no limit.
			 -->

		<!-- maxIdle: Maximum number of idle database connections to retain in pool.
			 Set to -1 for no limit.  See also the DBCP documentation on this
			 and the minEvictableIdleTimeMillis configuration parameter.
			 -->

		<!-- maxWaitMillis: Maximum time to wait for a database connection to become available
			 in ms, in this example 10 seconds. An Exception is thrown if
			 this timeout is exceeded.  Set to -1 to wait indefinitely.
			 -->

		<!-- username and password: MySQL username and password for database connections  -->

		<!-- driverClassName: Class name for the old mm.mysql JDBC driver is
			 org.gjt.mm.mysql.Driver - we recommend using Connector/J though.
			 Class name for the official MySQL Connector/J driver is com.mysql.jdbc.Driver.
			 -->

		<!-- url: The JDBC connection url for connecting to your MySQL database.
			 -->

	  <Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
				   maxTotal="100" maxIdle="30" maxWaitMillis="10000"
				   username="javauser" password="javadude" driverClassName="com.mysql.jdbc.Driver"
				   url="jdbc:mysql://localhost:3306/javatest" />

	</Context>
	
Depois de configurado o Resource no contexto, deve-se configurar um resource-ref no web.xml da aplicação que irá fazer uso do datasource:

	<web-app ...>
	  ...
	  <resource-ref>
		  <description>DB Connection</description>
		  <res-ref-name>jdbc/TestDB</res-ref-name>
		  <res-type>javax.sql.DataSource</res-type>
		  <res-auth>Container</res-auth>
	  </resource-ref>
	</web-app>
	
Agora uma aplicação pode obter o datasource via JNDI, através de lookup em `jdbc/TestDB`.

Veja exemplos de aplicações teste para vários bancos de dados em http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html

##Tomcat JDBC Connection Pool

Uma alternativa mais nova à solução DBCP. A configuração é idêntica, mas é preciso acrescentar o atributo `factory` indicando essa implementação, que não é default.

	  <Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
				   maxTotal="100" maxIdle="30" maxWaitMillis="10000"
				   username="javauser" password="javadude" driverClassName="com.mysql.jdbc.Driver"
				   url="jdbc:mysql://localhost:3306/javatest"
                         factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"/>

Veja mais em http://tomcat.apache.org/tomcat-8.0-doc/jdbc-pool.html