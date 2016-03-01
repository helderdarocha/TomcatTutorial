#10 Valves

Uma válvula (Valve) no Tomcat é um componente de interceptação de requisições. É inserido no pipeline de processamento de um container  (pode ser usado no escopo de um Engine, Host ou Context).

## Como usar uma válvula
Para usar uma válvula é preciso usar o elemento Valve e pelo menos o atributo className, que deve conter o nome completo de uma implementação da interface Valve:

    <Valve className="org.apache.catalina.valves.AccessLogValve" ... />
    
Dependendo da Valve usada, outros atributos poderão ser obrigatórios (um AccessLogValve, por exemplo, normalmente requer o uso de atributos para prefixo de arquivo de log, sufixo, diretório e um pattern para formatar a saída). Veja a documentação em http://tomcat.apache.org/tomcat-8.0-doc/config/valve.html para detalhes sobre a configuração de cada tipo de Valve.

##Válvulas disponíveis no Tomcat 8

Implementações nativas da interface Valve estão disponíveis no pacote org.apache.catalina.valves. O Tomcat 8 inclui válvulas para:

* Log de acesso: `AccessLogValve`, `ExtendedAccessLogValve`
* Controle de acesso: `RemoteAddressFilter`, `RemoteHostFilter`
* Suporte a proxies: `RemoteIpValve`, `SSLValve`
* Single Sing-On: `SingleSignOnValve`
* Autenticação: `BasicAuthenticatorValve`, `DigestAuthenticatorValve`, `FormAuthenticatorValve`, `SSLAuthenticatorValve`, `SPNEGOValve`
* Outros: `ErrorReportValve`, `CrawlerSessionManagerValve`, `StuckThreadDetectionValve`, `SemaphoreValve`

