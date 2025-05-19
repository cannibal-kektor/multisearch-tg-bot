FROM maven:3-eclipse-temurin-24 AS builder
#WORKDIR /usr/src/telegramBot
WORKDIR /opt/build/app
COPY pom.xml .
WORKDIR bot
COPY bot/pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:resolve-plugins dependency:resolve
COPY bot/ .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

FROM eclipse-temurin:24-jdk AS optimizer
WORKDIR /opt/build
COPY --from=builder /opt/build/app/bot/target/*.jar multisearchBot.jar
#RUN java -Djarmode=layertools -jar telegramBot.jar extract
RUN java -Djarmode=tools -jar multisearchBot.jar extract --layers --destination extracted

FROM eclipse-temurin:24-alpine
ARG BUILD_PATH=/opt/build/extracted
RUN addgroup -S telegramApp \
   && adduser -S --shell /bin/bash -G telegramApp telegramApp
#RUN groupadd --gid 999 telegramApp \
#  && useradd -rm -d /home/telegramApp -s /bin/bash -g telegramApp -u 999 telegramApp
USER telegramApp
WORKDIR /app

#COPY --from=optimizer $BUILD_PATH/jdk $JAVA_HOME
COPY --from=optimizer $BUILD_PATH/dependencies/ ./
COPY --from=optimizer $BUILD_PATH/spring-boot-loader/ ./
COPY --from=optimizer $BUILD_PATH/snapshot-dependencies/ ./
COPY --from=optimizer $BUILD_PATH/application/ ./

ARG BOT_TOKEN=${BOT_TOKEN}
ARG BOT_USERNAME=${BOT_USERNAME}
ARG BOT_CREATOR=${BOT_CREATOR}
ARG AUTH_PATH=/app/testCert

RUN --mount=from=cert_conf,target=/app/cert_conf sh cert_conf/initTestCertificates.sh

############AOT###########################
# Execute the AOT cache training run
#RUN java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -Dspring.context.exit=onRefresh -jar multisearchBot.jar
#RUN java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar multisearchBot.jar && rm app.aotconf
#RUN --mount=type=bind,src=../certificates/app,dst=/app/certs java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -Dspring.context.exit=onRefresh -jar multisearchBot.jar
#RUN --mount=type=bind,src=../certificates/app,dst=/app/certs java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar multisearchBot.jar && rm app.aotconf

# Start the application jar with AOT cache enabled - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and AOT cache friendly
#ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "multisearchBot.jar"]
#########################

# Execute the CDS training run
RUN java -XX:ArchiveClassesAtExit=multisearchBot.jsa -Dspring.context.exit=onRefresh -jar multisearchBot.jar && rm -r testCert

# Start the application jar with CDS enabled - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and CDS friendly
ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchBot.jsa", "-jar", "multisearchBot.jar"]
#ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchBot.jsa", "-Xlog:class+load:file=cds.log", "-jar", "multisearchBot.jar"]

#ENTRYPOINT ["java", "-jar", "multisearchBot.jar"]
