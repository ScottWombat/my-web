Remote deployment to Tomcat:

1. Add manager.xml host-manager to /opt/tomcat/conf/Catalina/localhost/
manager.xml
<Context privileged="true" antiResourceLocking="false" 
         docBase="{catalina.home}/webapps/manager">
    <Valve className="org.apache.catalina.valves.RemoteAddrValve" allow="^.*$" />
</Context>

host-manger.xml

<Context privileged="true" antiResourceLocking="false" 
         docBase="{catalina.home}/webapps/host-manager">
    <Valve className="org.apache.catalina.valves.RemoteAddrValve" allow="^.*$" />
</Context>

2. Add host-manager.xml to /opt/tomcat/conf/Catalina/localhost/

3. User must be 'manager-script' role