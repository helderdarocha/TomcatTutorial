#8 JNDI

Tomcat oferece um contexto JNDI InitialContext, que pode ser usado por aplicações Web para ter acesso a recursos disponíveis no registro, usando um lookup padrão JNDI.

Recursos podem ser definidos e declarados de forma estática usando web.xml ou context.xml.

##Registro JNDI via web.xml
Os elementos disponíveis em web.xml para registrar informações via JNDI são:

* `<env-entry>` - Permite registrar uma informação associada a um nome usado para referenciá-la (ex: um valor, um caminho, nome de arquivo, etc.)
* `<resource-ref>` - Permite registrar um resource (geralmente uma fábrica de conexões JDBC, sessão de JavaMail, etc.)
* `<resource-env-ref>` - Similar a `resource-ref`e mais simples de usar para resources que não requerem autenticação. 

Alguns dos elementos podem ser resolvidos automaticamente pelo Tomcat. Outros podem requerer configuração adicional no Tomcat (feita em `<Context>`)

##Registro JNDI via Context

Configuração de recursos específica do Tomcat é realizada em um elemento `<Context>` usando sub-elementos:

* `<Environment>` - Nomes e valores (escalares - strings. números, caminhos, etc.) que serão expostos via contexto JNDI (equivalente a `<env-entry>` no web.xml).
* `<Resource>` - Nome e tipo de dados de um resource disponibilizado à aplicação. (equivalente a `<resource-ref>` do web.xml).
* `<ResourceLink>` - Um vínculo para resource definido no contexto JNDI global. Necessário para dar à aplicação Web acesso a recurso definido em `<GlobalNamingResources>`.
* `<Transaction>` - Adiciona fábrica para instanciar o objeto UserTransaction (acessível via java:comp/UserTransaction).

##Registro JNDI via GlobalNamingResources

Um namespace de recursos globais disponível para o servidor inteiro. Os recursos declarados aqui podem ser expostos às aplicações Web usando um `<ResourceLink>`.

##Como usar um recurso
Exemplo de acesso a um datasource:

    Context initCtx = new InitialContext();
    Context envCtx = (Context) initCtx.lookup("java:comp/env");

    DataSource ds = (DataSource)
      envCtx.lookup("jdbc/EmployeeDB");

    Connection conn = ds.getConnection();
    // usar a conexão
    conn.close();

##Exemplos

Como registrar um bean, data sources, sessões de JavaMail e outros recursos. Veja exemplos na documentação oficial em 
http://tomcat.apache.org/tomcat-8.0-doc/jndi-resources-howto.html

