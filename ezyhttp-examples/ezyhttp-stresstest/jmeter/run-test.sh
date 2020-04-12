# config: 
# - jmeter.properties: remote_hosts=127.0.0.1
# - user.properties: server.rmi.ssl.disable=true
jmeter -Djava.rmi.server.hostname=127.0.0.1 -s
jmeter -n --testfile test.jmx -l result.jtl -r