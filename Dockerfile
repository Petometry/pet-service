FROM openjdk:21
EXPOSE 8080
ARG JAR_NAME
ADD /target/service.jar /service.jar
CMD java  -jar /service.jar