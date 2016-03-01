#13 Load balancing

Para balancear a carga de duas instâncias rodando em hosts diferentes, é preciso garantir que cada instância possua um conector de redirecionamento ativo e cada Engine possua um identificador que será usado para balanceamento (atributo `jvmRoute`). O nome pode ser arbitrário. Por exemplo, para um servidor em node1.dominio.com poderia ser:

    <Engine name="Catalina" defaultHost="localhost" jvmRoute="node1">
      ...  
    </Engine>
    
e para o servidor em node2.dominio.com:
    
    <Engine name="Catalina" defaultHost="localhost" jvmRoute="node2">
      ...  
    </Engine>

##Configuração usando Apache e mod_jk

Verifique que existe em cada instância um conector AJP para a porta 8009.

Veja seção sobre Conectores, para instalar e configurar o `mod_jk`. A configuração do plugin é semelhante, mas o arquivo workers.properties, que vai conter as regras de balanceamento, é diferente;

Faça uma cópia do arquivo `other/jk.conf` mostrado na seção sobre conectores, e depois troque a extensão de jk.conf para que ele não seja carregado pelo httpd.conf (ex: jk.conf-basic). Troque o nome do JkWorkersFile para balanced-workers.properties:

    # Carregar o módulo
    LoadModule jk_module libexec/apache2/mod_jk.so
    
    # Workers e arquivos compartilhados
    JkWorkersFile /etc/apache2/other/balanced-workers.properties
    JkShmFile /var/log/apache2/mod_jk.shm
    
    # Config de log
    JkLogFile /var/log/apache2/mod_jk.log
    JkLogLevel info
    JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "
 
    JkMount /* servidor
    
Agora faça uma cópia de workers.properties com o nome de `balanced-workers.properties`, onde iremos colocar as regras de balanceamento para dois servidores (`node1` e `node2`):

    # servidor é o nome do load balancer
    worker.list=servidor
    
    #servidor 1
    worker.node1.port=8009
    worker.node1.type=ajp13
    worker.node1.host=node1.dominio.com
    worker.node1.lbfactor=1
    worker.node1.cachesize=10
    
    #servidor 2
    worker.node2.port=8009
    worker.node2.type=ajp13
    worker.node2.host=node2.dominio.com
    worker.node2.lbfactor=1
    worker.node2.cachesize=10
    
    # load balancer
    worker.servidor.type=lb
    worker.servidor.balance_workers=node1,node2
    worker.servidor.sticky_session=1
    
A configuração acima realiza balanceamento de carga alternado (round-robin) entre dois servidores. O `lbfactor` é o peso de cada servidor, que aqui está igual. Ele pode ser usado para dar prioridade a um nó em detrimento de outro. Quanto maior o número em relação aos outros, mais requisições ele irá receber. Isto pode ser usado para diferenciar servidores com mais poder de processamento.

O atributo `cachesize` define o tamanho dos thread pools associados ao container (a quantidade de requisições concorrentes que ele irá enviar ao servidor). É importante que esse número não supere o número de threads configurado no Conector em server.xml.

A propriedade `sticky_session` garante que diferentes requisições para a mesma sessão sejam enviadas para o mesmo servidor. Se o valor for 0, isso não será respeitado e a sessão perderá requisições.

Reinicie o Apache para gravar as alterações.

Para testar, execute as instâncias, e envie requisições para o servidor Apache. As requisições devem ser processadas de forma alternada.

##Configuração usando Apache e mod_proxy

Verifique que mod_proxy e mod_proxy_balancer estejam habilitados no Apache. 

    LoadModule proxy_module modules/mod_proxy.so
    LoadModule proxy_balancer_module modules/mod_proxy_balancer.so

Há três algoritmos de balanceamento que podem ser usados: byrequests, bytraffic e bybusiness através da propriedade `lbmethod`. A propriedade `stickysession` deve conter o nome do cookie. O fragmento abaixo ilustra como poder ser implementado um balanceamento simples entre dois hosts usando o `mod_proxy`:

    <IfModule proxy_module>
        ProxyRequests Off
        ProxyPass / balancer://mycluster 
        ProxyPassReverse / balancer://mycluster 
        <Proxy balancer://mycluster>
             BalancerMember ajp://node1.domain.com:8009 route=node1 loadfactor=1
             BalancerMember ajp://node2.domain.com:8009 route=node2 loadfactor=1

             ProxySet lbmethod=bybusyness
             ProxySet stickysession=JSESSIONID

             Order Deny,Allow
             Deny from none
             Allow from all
        </Proxy>
     </IfModule>
 
Veja mais em https://httpd.apache.org/docs/2.4/mod/mod_proxy_balancer.html
