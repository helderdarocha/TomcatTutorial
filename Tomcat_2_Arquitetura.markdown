#2 Arquitetura

Tomcat é um servidor Web. O termo servidor (server) representa a máquina (host) onde rodam diversos componentes, que oferecem serviços (service). Cada serviço consiste de um ou mais processadores (engine) que o cliente acessa através de um conector (connector). Diversos aspectos desses componentes podem ser customizados.

##Principais componentes

Tomcat contém diversos componentes de serviço. Os mais importantes são:

**Catalina** é o componente principal do Tomcat e implementação padrão de um container Web Java EE que adere à especificação Servlet, que é a base de todas as aplicações Web em Java (inclusive JSP, JSF, etc.) Catalina é, portanto, um Servlet Container e é o primeiro serviço que é iniciado no Tomcat. Além de container para Web apps, a configuração do serviço e os aspectos relativos a segurança das aplicações são responsabilidade do componente Catalina.

**Coyote** é o componente do Tomcat que suporta o protocolo HTTP 1.1 como um servidor Web. Isto permite que Catalina também acumule a função de servir eficientemente páginas HTML, aplicações CGI, e todas as funções de um servidor Web como o Apache Web Server. 

O componente **Coyote JK** oferece a mesma interface idêntica de servidor Web HTTP para o Catalina, mas em vez de processar os dados HTTP internamente, redireciona através do protocolo JK (AJP - Apache JServ Protocol) para um outro servidor, geralmente o Apache Web Server.

**Jasper** é o processador (parser/compilador) JSP que interpreta e compila código JSP, taglibs, JSTL e gerencia-os em um pool para reuso.

Além desses componentes, Tomcat contém vários outros que cuidam do deployment de aplicações Web (contextos), clustering e alta disponibilidade.

##Servidor de aplicações
**Apache TomEE ** é um servidor de aplicações Java EE baseado no Apache Tomcat que combina vários projetos open source oferecendo suporte a EJB, CDI, JPA, JMS, REST, SOAP, JTA e JSF.

##Estrutura e variáveis de ambiente
A estrutura de uma instalação do Tomcat depende de como é instalado (standalone, múltiplas instâncias, serviço, cluster, embutido).

Há duas variáveis de ambiente que são usadas para representar o local onde o servidor está instalado:

* `$CATALINA_HOME` - raiz da instalação
* `$CATALINA_BASE` - raiz de cada instância instalada (se só houver apenas uma instância instalada, é igual a `$CATALINA_HOME`)

Outras variáveis de ambiente e diferentes instalações serão detalhadas mais adiante. 

Em todas as instalações há uma estrutura padrão de diretórios dentro de $CATALINA_HOME:

* `/bin` - contém os scripts para iniciar, reiniciar, finalizar o Tomcat e outros recursos
* `/conf `- contém arquivos de configuração, dentre eles o server.xml que é o arquivo de configuração principal
* `/logs` - diretório default para armazenar arquivos de log
* `/webapps` - contém as aplicações web (contextos)

Os arquivos de configuração são lidos na inicialização do servidor. Se forem feitas alterações, o servidor precisará ser reinicializado.

##Server, Engine, Host e Context

Esta é a hierarquia mais importante dos componentes do Tomcat:

Um servidor (**Server**) pode ter
  um ou mais processadores (**Engine**), associados a
    um ou mais endereços Web (**Host**), que contém
      um ou mais aplicações (**Context**)
      
O Tomcat permite diversas configurações em cada um desses componentes.

##Instalação default

Na instalação default, o Tomcat está pronto para rodar aplicações. A instalação pode ser feita rapidamente através de *hot deployment*: copiando um WAR (componente padrão Java EE) para dentro da pasta `/webapps` enquanto o servidor estiver rodando. Dentro de segundos o WAR será aberto e a aplicação será instalada. 

Em uma instalação default do Tomcat, a aplicação, se estiver construída corretamente, poderá pouco depois da cópia ser acessada pelo endereço:

    http://localhost:8080/contexto

Onde `contexto` é o nome do arquivo WAR, sem a extensão `.war`.