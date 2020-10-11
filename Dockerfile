FROM adoptopenjdk/openjdk11:alpine-slim as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY www_root www_root

RUN ./mvnw install -DskipTests

FROM adoptopenjdk/openjdk11:alpine-slim
VOLUME /tmp
COPY --from=build /workspace/app/target/lib /app/lib
COPY --from=build /workspace/app/www_root /www_root
COPY --from=build /workspace/app/target/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.lemberski.webserver.App"]