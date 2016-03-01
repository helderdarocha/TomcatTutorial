#11 Conectores

Um conector (Connector) representa uma porta que o Tomcat usará para monitorar requisições do cliente. Conectores podem criados dentro de serviços (Service) e processadores (Engine) e roteiam requisições do cliente para serviços que possam processá-los. Conectores também podem ser usados para conectar o Tomcat a outros serviços externos, como por exemplo, um servidor Apache, e são essenciais no balanceamento de cargas.

A finalidade de um conector é de apenas monitorar uma porta a espera de uma requisição, e ao receber a requisição, passá-la para um processador (Engine), depois retornar os resultados para a porta. A sintaxe básica é:

    <Connector port="porta-de-serviço" />

##Como configurar hierarquias de conectores
O exemplo abaixo (mulesoft.com) ilustra uma configuração simplificada com dois conectores declarados:

    <Server>
        <Service>
            <Connector port="8443"/>
            <Connector port="8444"/>
            
            <Engine>
                 <Host name="yourhostname">
                    <Context path="/webapp1"/>
                    <Context path="/webapp2"/>
                 </Host>
            </Engine>
            
        </Service>
    </Server>

Os dois conectores acima operam no mesmo serviço, e cada serviço contém um processador (Engine). Ambos irão repassar as requisições para o mesmo processador, que passará para ambas as aplicações (Context). Cada requisição, portanto, irá provavelmente gerar duas respostas (uma de cada aplicação).

Para que cada conector conecte-se a um contexto distinto, é necessário ter serviços/processadores distintos. Para isto podemos alterar a hieraquia da forma abaixo, usando um conector para cada serviço:

    <Server>
        <Service name="Catalina">
            <Connector port="8443"/>
            
            <Engine>
                 <Host name="yourhostname">
                    <Context path="/webapp1"/>
                 </Host>
            </Engine>
            
        </Service>
        <Service name="Catalina-2">
            <Connector port="8444"/>
            
            <Engine>
                 <Host name="yourhostname">
                    <Context path="/webapp2"/>
                 </Host>
            </Engine>
            
        </Service>
    </Server>

Toda a configuração relacionada a conectores envolve configurações da hierarquia de Service/Engine/Host/Context.

##Tipos de conectores

Há dois tipos de conectores em Tomcat:

* **HTTP** - conector que responde ao protocolo HTTP; possui atributos para diversas funções como redirecionamento e proxy. O tipo é identificado pelo atributo `protocol` que normalmente é `HTTP/1.1` (default, se ausente). Outro atributo importante é `SSLEnabled` que, se `true` ativa a camada SSL na requisição/resposta. Pode-se implementar load balancing com este conector (via `mod_proxy`), mas pé menos eficiente que AJP.
* **AJP** - é um conector que responde ao protocolo `AJP` - Apache JServ Protocol, que é uma versão otimizada do HTTP tipicamente usada para permitir que o Tomcat se comunique com um servidor Apache. Conectores AJP são geralmente implementados via plugin `mod_jk`, mais eficiente. Use `protocol="AJP/1.3"`

Os conectores também possuem implementações alternativas usando protocolos nativos (APR), NIO e BIO (Non-blocking e Blocking IO). Use o nome da classe que implementa o serviço no atributo `protocol`.

O componente chamado de Coyote usa um conector HTTP e oferece suporte nativo a HTTP. O componente chamado de Coyote JK, é um conector AJP e usa o protocolo JK para redirecionar o serviço.

##Como conectar o Tomcat ao Apache Web Server

São bibliotecas nativas do Apache 2.4 os módulos:

* `mod_proxy_http`
* `mod_proxy_ajp`

A principal vantagem é que fazer parte da distribuição do Apache e não precisam ser instalados separadamente. Suportam tanto HTTP como AJP. Por outro lado, mod_proxy_http é pouco eficiente.

Para muitos casos, o uso de mod_proxy_ajp é uma solução boa, pois utiliza AJP (mais eficiente que HTTP) e não requer configuração adicional. Uma desvantagem é que é mais novo e ainda tem muitos bugs, e algumas limitações (como tamanho máximo de pacotes <8k).

Uma alternativa é `mod_jk`, que tem como desvantagem a necessidade de instalar e configurar separadamente, mas é a solução mais estável no momento e recomendada pela documentação do Tomcat.

Existem vários outros módulos que podem funcionar mas não são mais suportados (mod_proxy, mod_jk2, mod_jserv, mod_webapp, etc.)

Uma discussão sobre quais módulos usar pode ser encontrada aqui: http://www.tomcatexpert.com/blog/2010/06/16/deciding-between-modjk-modproxyhttp-and-modproxyajp

Para qualquer tipo de conexão, é necessário ter o conector ativo no Tomcat. Verifique se o conector AJP está habilitado no server.xml (geralmente está habilitado por default na porta 8009):

    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

Em seguida instale um módulo do Apache.

##Conector usando mod_proxy_ajp

Veja mais informações sobre este conector em http://httpd.apache.org/docs/current/mod/mod_proxy_ajp.html
    
Para usar um módulo nativo do Apache, como o `mod_proxy_ajp` é preciso apenas configurar no httpd.conf do Apache. Verifique que os módulos estejam habilitados (exemplo no MacOS):

    LoadModule proxy_module libexec/apache2/mod_proxy.so
    LoadModule proxy_ajp_module libexec/apache2/mod_proxy_ajp.so

No final do arquivo httpd.conf há uma linha para incluir configurações externas (se não houver, crie):

    Include /private/etc/apache2/other/*.conf

Crie na pasta `other` um arquivo `ajp.conf` onde iremos colocar a configuração do AJP. Neste arquivo, configure o módulo:

    ProxyRequests Off
    <Proxy *>
        Order deny,allow
        Deny from all
        Allow from localhost
    </Proxy>
    ProxyPass                 /     ajp://localhost:8009/
    ProxyPassReverse    /     ajp://localhost:8009/

O caminho `/` redireciona todas as chamadas para o Tomcat. Pode-se também restringir o redirecionamento a um contexto específico:

    ProxyPass                 /teste     ajp://localhost:8009/teste
    ProxyPassReverse    /teste     ajp://localhost:8009/teste

Reinicie o servidor Apache (`sudo httpd -k restart` ou via serviço) e teste a conexão.

##Conector usando mod_jk

O `mod_jk` não é nativo do Apache (precisa ser baixado, compilado e instalado separadamente), mas tem mais recursos e mais estável em plataformas Linux, além de ser mais fácil de instalar (via `apt-get`). Por outro lado, apresenta bugs em Mac OS 10.11, onde precisa ser baixado, compilado e instalado manualmente.

O mod_jk é configurado através de um arquivo `workers.properties` que define o protocolo usado, porta e host para conexão. O arquivo abaixo possui uma configuração mínima para a conexão:

    worker.list=servidor
    worker.servidor.type=ajp13
    worker.servidor.host=localhost
    worker.servidor.port=8009
    
Grave o `workers.properties` na pasta `/etc/apache2/other/` e crie um arquivo `jk.conf` para configurar o módulo:

    # Carregar o módulo
    LoadModule jk_module libexec/apache2/mod_jk.so
    
    # Workers e arquivos compartilhados
    JkWorkersFile /etc/apache2/other/workers.properties
    JkShmFile /var/log/apache2/mod_jk.shm
    
    # Config de log
    JkLogFile /var/log/apache2/mod_jk.log
    JkLogLevel info
    JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "
 
    JkMount /* servidor
    
Reinicie o servidor Apache (`sudo httpd -k restart` ou via serviço) e teste a conexão.