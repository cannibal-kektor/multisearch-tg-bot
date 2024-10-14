FROM maven:latest AS builder
#WORKDIR /usr/src/telegramBot
WORKDIR /opt/build/app
#COPY pom.xml .
#COPY ./telegramBot/pom.xml ./telegramBot
#COPY ./botProcessor/pom.xml ./botProcessor
#RUN mvn -B -f pom.xml -pl telegramBot -am -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
WORKDIR /opt/build/app/bot
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml -DskipTests clean package


FROM eclipse-temurin:21 AS optimizer

WORKDIR /opt/build
COPY --from=builder /opt/build/app/bot/target/*.jar multisearchBot.jar
#RUN java -Djarmode=layertools -jar telegramBot.jar extract
RUN java -Djarmode=tools -jar multisearchBot.jar extract --layers --launcher

FROM eclipse-temurin:21
#FROM openjdk:21-jdk-slim-buster
ARG BUILD_PATH=/opt/build/multisearchBot

RUN groupadd --gid 999 telegramApp \
  && useradd -rm -d /home/telegramApp -s /bin/bash -g telegramApp -u 999 telegramApp
USER telegramApp:telegramApp
#ENV BOT_TOKEN=${BOT_TOKEN}
#ENV BOT_USERNAME=${BOT_USERNAME}
#ENV BOT_CREATOR=${BOT_CREATOR}

WORKDIR /opt/workspace

#COPY --from=optimizer $BUILD_PATH/jdk $JAVA_HOME
COPY --from=optimizer $BUILD_PATH/spring-boot-loader/ ./
COPY --from=optimizer $BUILD_PATH/dependencies/ ./
#COPY --from=optimizer $BUILD_PATH/snapshot-dependencies/ ./
COPY --from=optimizer $BUILD_PATH/application/ ./

#CMD ["--username=Fastsearch_test_bot","--token=7038976475:AAHPmKVsPZ7MLjnTnl9nBpCwMoSLCIIrsf4","--creator=1295143434"]
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

#---------------------------------------------------------
#FROM eclipse-temurin:21
##FROM openjdk:21-jdk-slim-buster
#
##RUN adduser -Dh /home/gordon gordon
#RUN groupadd --gid 1000 telegramApp \
#  && useradd -rm -d /home/telegramApp -s /bin/bash -g telegramApp -u 1000 telegramApp
#USER telegramApp:telegramApp
#WORKDIR /opt/workspace
#COPY --from=telegramBot /opt/build/telegramApp/telegramBot/target/telegramBot-0.0.1-SNAPSHOT.jar .
#CMD ["--Username=Fastsearch_test_bot","--Token=7038976475:AAHPmKVsPZ7MLjnTnl9nBpCwMoSLCIIrsf4","--CreatorId=1295143434"]
#ENTRYPOINT ["java", "-jar", "/opt/workspace/telegramBot-0.0.1-SNAPSHOT.jar"]
##CMD ["--spring.profiles.active=postgres"]
