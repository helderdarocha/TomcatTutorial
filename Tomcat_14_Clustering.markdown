#14 Clustering
A configuração de Cluster oferece muitas opções e é bastante complexa, mas a configuração default muitas vezes requer nenhuma ou poucas alterações para a maior parte dos casos.

O elemento `<Cluster>` pode ser colocado no `<Engine>` ou no `<Host>`. 

    <Engine>
    ...
        <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"/>
    </Engine>

A configuração default, replica *todas* as sessões em *todas* as instâncias do cluster. Ideal para um cluster pequeno e quando a aplicação está *instalada em todos eles*.

Pode-se configurar as propriedades do cluster, através da configuração de sub-elementos de `<Cluster>`:  `<Manager>`, `<Channel>`, `<Valve>`, `<Deployer>`, etc.

##Componentes de Cluster

###Cluster
`<Cluster>` é o elemento principal, dentro do qual os detalhes podem ser configurados. 

###Manager de sessões
A replicação da sessão é realizada pelo Manager. Por default, o Cluster define um `<Manager>` caso não tenha um declarado explicitamente usando a implementação `DeltaManager`. A configuração default é

    <Manager className="org.apache.catalina.ha.session.DeltaManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"/>

Esse manager replica a sessão para todos os nós do cluster. Funciona bem para clusters pequenos, mas em clusters grandes pode ser um problema. Replica inclusive para nós que não têm a aplicação que usará a sessão instalado. Para lidar com isso, deve-se usar o BackupManager:

    <Manager className="org.apache.catalina.ha.session.BackupManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"
                   mapSendOptions="6"/>

O `<Manager>` é o template definido para todas as aplicações Web marcadas como `<distributable/>` no seu web.xml.

###Requisitos para replicação de sessões

* Garantir que todos os atributos da sessão sejam serializáveis (precisam implementar java.io.Serializable)
* web.xml deve ter elemento `<distributable />`
* Garantir que as URLs de uma sessão sejam idênticas (se houver qualquer diferença, uma nova sessão será criada, e não replicada)
* Incluir (descomentar) elemento `<Cluster>` em server.xml e configurar se necessário.
* Se usado com servidor Apache via mod_jk, é preciso configurar atributo jvmRoute:
`<Engine name="Catalina" jvmRoute="tomcat_1" >`
onde tomcat_1 é o nome do worker em `workers.properties`
* Se usado com balanceamento de cargas, configurar o balanceador no modo de Sticky Session.
* Se nós do cluster estiverem na mesma máquina, deve-se configurar  `<Receiver>`  para cada `<Channel>` dentro de `<Cluster>` usando um valor diferente para o atributo `port`.

###Channel
Channel e seus sub componentes são parte da camada IO do grupo do cluster, o módulo de comunicação Apache Tribes. É um framework de mensageria de baixo acoplamento.

Channel gerencia um conjunto de subcomponentes que juntos criam um framework de mensageria usado internamente pelos componentes que precisam enviar mensagens entre diferentes instâncias.

    <Channel className="org.apache.catalina.tribes.group.GroupChannel">
    ...
    </Channel>

Channel contém quatro componentes importantes:

**Membership** - componente responsavel por automaticamende descobrir novos nós no cluster e prover notificações para nós que não deram sinal de vida. Implementação usa multicast. Pode-se dividir clusters em grupos que recebem heartbeats diferentes, mudando o endereço de multicast.

    <Membership className="org.apache.catalina.tribes.membership.McastService"
                        address="228.0.0.4"
                        port="45564"
                        frequency="500"
                        dropTime="3000"/>
                        
**Sender** - componente que gerencia todas as conexões de saída e mensagens de dados que são enviados pela rede de um nó para outro. Este componente permite que mensagens sejam enviadas em paralelo. Sender contém o componente **Transport**, que é a camada IO de mais baixo nível. A implementação default usa sockets non-blocking.

        <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
              <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
        </Sender>

**Receiver** - monitora a chegada de mensagens enviadas por outros nós. Aqui pode-se configurar o thread pool do cluster, já que mensagens entrantes serão processadas mais rapidamente em um pool.

    <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                      address="auto"
                      port="5000"
                      selectorTimeout="100"
                      maxThreads="6"/>
                      
**Interceptor** - pode-se incluir interceptadores para controlar a maneira como as mensagens são enviadas e recebidas, dentre outras configurações.

     <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
     <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatch15Interceptor"/>
     <Interceptor className="org.apache.catalina.tribes.group.interceptors.ThroughputInterceptor"/>

###Valve
A implementação de Cluster em Tomcat usa Valves para monitorar quando as requisições entram e saem do container. As Valves usadas em clusters são implementações da interface ClusterValve e são três:

* `ReplicationValve` - notifica o cluster no final da requisição para que ele possa decidir se há dados a serem replicados
* `JvmBinderValve` - se acontecer um failover de mod_jk, o valve irá substituir o atributo jvmWorker no ID da sessão, para que requisições futuras sejam direcionados a esse nó. 
* `ClusterSingleSignOn` - suporta SSO no cluster. A identidade de segurança autenticada por uma aplicação web é reconhecida pelas outras aplicações web no mesmo host virtual e propagada para os outros nós do cluster.
* 

     <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
                 filter=".*\.gif|.*\.js|.*\.jpeg|.*\.jpg|.*\.png|.*\.htm|.*\.html|.*\.css|.*\.txt"/>


###Deployer
Este componente permide que aplicações sejam instaladas nos servidores participantes do cluster (Farm Deployer).

    <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
                    tempDir="/tmp/war-temp/"
                    deployDir="/tmp/war-deploy/"
                    watchDir="/tmp/war-listen/"
                    watchEnabled="false"/>

##Cenário com cluster

Duas instâncias. Sequência de eventos. Veja http://tomcat.apache.org/tomcat-8.0-doc/cluster-howto.html

1. TomcatA starts up
2. TomcatB starts up (Wait that TomcatA start is complete)
3. TomcatA receives a request, a session S1 is created.
4. TomcatA crashes
5. TomcatB receives a request for session S1
6. TomcatA starts up
7. TomcatA receives a request, invalidate is called on the session (S1)
8. TomcatB receives a request, for a new session (S2)
9. TomcatA The session S2 expires due to inactivity.

##Configuração de um cluster com mod_jk

https://www.mulesoft.com/tcat/tomcat-clustering
http://blog.c2b2.co.uk/2014/04/how-to-set-up-cluster-with-tomcat-8.html

