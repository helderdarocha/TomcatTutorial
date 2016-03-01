#5 Aplicações

O Tomcat é uma aplicação que serve aplicações Web que aderem à especificação Java EE para aplicações Web. Aplicações típicas que são instaladas no Tomcat usam as tecnologias servlets, JSP, JSF, WebSockets, SOAP, REST, etc.

##Web app
Uma aplicação Web padrão pode ser representada como um arquivo ZIP comprimido com extensão WAR, ou como um diretório. Ambos devem ter uma estrutura padrão com subdiretórios específicos para páginas estáticas, biblitecas (JARs), classes Java e arquivos de configuração. Essa estrutura é descrita abaixo:

* Raiz do contexto - é a pasta ou WAR que contém tudo. Deve conter os arquivos HTML, JSP e outros que serão acessados diretamente a partir do contexto da aplicação.
* /WEB-INF - é uma pasta privativa que não pode ser acessada pelo cliente. Quaisquer arquivos que estiverem aqui não poderão ser transferidos pelo servidor.
* /WEB-INF/web.xml - o Web Application Deployment Descriptor padrão para a aplicação. Ele contém informações de configuração, servlets, filtros e outros componentes.
* /WEB-INF/classes - É o classpath para classes Java.
* /WEB-INF/lib - O conteúdo de todos os JARs contidos nesta pasta será acrescentado ao classpath da aplicação.

A estrutura do Web App pode ser montada de qualquer maneira, ou podem-se usar ferramentas como Ant e Maven e IDEs para este fim. Pode-se ainda ter uma pasta /META-INF com outros dados, tipicamente arquivos usados pelas aplicações, e `context.xml` usado pelo Tomcat. 

##Breve introdução a aplicações Web e Java EE
Se você não tem conhecimento de aplicações Java EE, servlets e JSP, veja uma breve apresentação.

##Aplicação exemplo

Veja como instalar uma aplicação exemplo.
