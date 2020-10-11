FROM adoptopenjdk/openjdk11:alpine-slim as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY www_root target/www_root

RUN ./mvnw install -DskipTests

FROM adoptopenjdk/openjdk11:alpine-slim
VOLUME /tmp
ARG TARGETDIR=/workspace/app/target
COPY --from=build ${TARGETDIR}/lib /app/lib
COPY --from=build ${TARGETDIR}/www_root /www_root
COPY --from=build ${TARGETDIR}/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.lemberski.webserver.App"]