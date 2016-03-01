#12 Logging

O Tomcat usa internamente o Apache Commons Logging modificado em um pacote chamado JULI. É possível configurar o Tomcat para usar o Log4J em vez do JULI.

Através do JULI qualquer aplicação pode usar a API java.util.logging ou a API da especificação Servlet de forma nativa.

##Console
Uma instalação default do Tomcat redireciona a saída do console para o arquivo catalina.out. Tudo o que for gravado em System.err ou System.out será redirecionado para este arquivo.

##Log de acesso
Logs de acesso no Tomcat são implementados como uma Válvula. Existem válvulas específicas para interceptar requisições e logar nos arquivos de acesso. Os nomes dos arquivos, prefixos e sufixos são definidos na configuração de cada Valve.

Veja mais informações sobre JULI em http://tomcat.apache.org/tomcat-8.0-doc/logging.html