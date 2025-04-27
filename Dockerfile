FROM tomcat:11-jdk21
LABEL authors="davu"

COPY target/users-service.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]