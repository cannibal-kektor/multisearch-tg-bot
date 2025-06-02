FROM maven:3-eclipse-temurin-24 AS builder
#WORKDIR /usr/src/telegramBot
WORKDIR /opt/build/app
COPY pom.xml .
WORKDIR processor
COPY processor/pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:resolve-plugins dependency:resolve
COPY processor/ .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

FROM eclipse-temurin:24-jdk AS optimizer
WORKDIR /opt/build
COPY --from=builder /opt/build/app/processor/target/*.jar multisearchProcessor.jar
#RUN java -Djarmode=layertools -jar telegramBot.jar extract
RUN java -Djarmode=tools -jar multisearchProcessor.jar extract --layers --destination extracted

FROM eclipse-temurin:24-alpine
ARG BUILD_PATH=/opt/build/extracted
RUN addgroup -S --gid 1000 telegramApp \
   && adduser -S --shell /bin/bash -G telegramApp --uid 1000 telegramApp
#RUN groupadd --gid 999 telegramApp \
#  && useradd -rm -d /home/telegramApp -s /bin/bash -g telegramApp -u 999 telegramApp
USER telegramApp
WORKDIR /app

#COPY --from=optimizer $BUILD_PATH/jdk $JAVA_HOME
COPY --from=optimizer $BUILD_PATH/dependencies/ ./
COPY --from=optimizer $BUILD_PATH/spring-boot-loader/ ./
COPY --from=optimizer $BUILD_PATH/snapshot-dependencies/ ./
COPY --from=optimizer $BUILD_PATH/application/ ./

#ARG BOT_TOKEN=${BOT_TOKEN}
#ARG BOT_USERNAME=${BOT_USERNAME}
#ARG BOT_CREATOR=${BOT_CREATOR}
#ARG BOT_TOKEN
#ARG BOT_USERNAME
#ARG BOT_CREATOR

RUN --mount=from=cert_conf,target=/app/cert_conf sh cert_conf/buildMockCertificates.sh

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
#https://github.com/spring-projects/spring-data-elasticsearch/issues/2876
ARG SPRING_DATA_ELASTICSEARCH_SKIP_REPOSITORY_INIT=true
RUN --mount=type=secret,id=bot_credentials,target=/run/secrets/bot_credentials,uid=1000,gid=1000,mode=0400 \
    java \
      -XX:ArchiveClassesAtExit=multisearchProcessor.jsa \
      -Dspring.context.exit=onRefresh \
      -Dspring.config.import=file:/run/secrets/bot_credentials[.properties] \
      -jar multisearchProcessor.jar && \
    rm -r certs
# Start the application jar with CDS enabled - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and CDS friendly
ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchProcessor.jsa", "-jar", "multisearchProcessor.jar"]
#ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchProcessor.jsa", "-Xlog:class+load:file=cds.log", "-jar", "multisearchProcessor.jar"]

#ENTRYPOINT ["java", "-jar", "multisearchProcessor.jar"]
