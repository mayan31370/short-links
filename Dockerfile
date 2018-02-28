FROM mayan31370/openjdk-alpine-with-chinese-timezone:8-jre
ENV SHORT_LINKS_REDIS_HOST localhost
ENV SHORT_LINKS_BASE_URL http://localhost:8080/
EXPOSE 8080
ENTRYPOINT ["java","-Xms128m","-Xmx128m","-jar","-Dserver.port=8080","/app.jar"]
ADD target/*.jar app.jar
