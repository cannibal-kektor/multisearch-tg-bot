FROM maven:3-eclipse-temurin-24 AS builder
WORKDIR /opt/build/app
COPY pom.xml .
WORKDIR processor
COPY processor/pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:resolve-plugins dependency:resolve
COPY processor/src src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

FROM eclipse-temurin:24-jdk-alpine AS optimizer
WORKDIR /opt/build
COPY --from=builder /opt/build/app/processor/target/*.jar multisearchProcessor.jar
RUN java -Djarmode=tools -jar multisearchProcessor.jar extract --layers --destination extracted
RUN jdeps --ignore-missing-deps -q \
    --recursive  \
    --multi-release 24  \
    --print-module-deps  \
    --class-path 'extracted/dependencies/lib/*':'extracted/snapshot-dependencies/lib/*' \
    multisearchProcessor.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output optimizedJRE24

FROM alpine/openssl

ENV JAVA_HOME /java/optimizedJRE24
ENV PATH $JAVA_HOME/bin:$PATH
ARG BUILD_PATH=/opt/build
ARG BUILD_EXTRACTED=$BUILD_PATH/extracted

WORKDIR /app
COPY --from=optimizer $BUILD_PATH/optimizedJRE24 $JAVA_HOME
COPY --from=optimizer $BUILD_EXTRACTED/dependencies/ ./
COPY --from=optimizer $BUILD_EXTRACTED/spring-boot-loader/ ./
COPY --from=optimizer $BUILD_EXTRACTED/snapshot-dependencies/ ./
COPY --from=optimizer $BUILD_EXTRACTED/application/ ./

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
RUN addgroup -S --gid 1000 telegramApp \
   && adduser -S --shell /bin/bash -G telegramApp --uid 1000 telegramApp
USER telegramApp

ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchProcessor.jsa", "-jar", "multisearchProcessor.jar"]
#ENTRYPOINT ["java", "-XX:SharedArchiveFile=multisearchProcessor.jsa", "-Xlog:class+load:file=cds.log", "-jar", "multisearchProcessor.jar"]