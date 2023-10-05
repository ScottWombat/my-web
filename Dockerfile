FROM tomcat:latest

COPY ./target/my-web.war /usr/local/tomcat/webapps

EXPOSE 8080

CMD [“catalina.sh”, “run”]