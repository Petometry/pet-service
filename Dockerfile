FROM openjdk:21
EXPOSE 8080
ARG JAR_NAME
ADD /target/service.jar /service.jar
ENV DATABASE_URL=""
ENV DATABASE_USERNAME=""
ENV DATABASE_PASSWORD=""
CMD java  -jar /service.jar