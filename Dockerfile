FROM openjdk:8-jdk-alpine
VOLUME /theskeleton
ARG JAR_FILE=*.jar
ADD target/${JAR_FILE} /theskeleton/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /theskeleton/app.jar
