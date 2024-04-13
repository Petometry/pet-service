FROM openjdk:21
EXPOSE 8080
ARG JAR_NAME
ADD /target/service.jar /service.jar
ENV database_url=""
ENV database_username=""
ENV database_password=""
ENV domain_base=""
ENV domain_prefix=""
CMD java  -jar /service.jar