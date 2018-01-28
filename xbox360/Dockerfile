FROM maven:3.5-jdk-8 AS builder

COPY . /build

WORKDIR /build

RUN mvn install

FROM openjdk:8

COPY --from=builder /build/target/xbox360-jar-with-dependencies.jar /app/run.jar
COPY ./entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["java", "-jar", "/app/run.jar"]