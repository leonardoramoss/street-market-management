FROM gradle:jdk11-hotspot AS cache

WORKDIR /build

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

ENV GRADLE_USER_HOME /cache

RUN gradle --no-daemon build --stacktrace

FROM gradle:jdk11-hotspot AS builder

RUN gradle --version && java -version

WORKDIR /build

COPY --from=cache /cache /home/gradle/.gradle

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src src

RUN gradle clean build --no-daemon

#
# RELEASE image
#
FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine AS release

COPY --from=builder /build/build/libs/*all.jar /service.jar

ENTRYPOINT ["java", "-jar", "/service.jar"]
