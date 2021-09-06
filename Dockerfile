FROM gradle:jdk11-hotspot AS build
WORKDIR /cache

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY ./src /cache/src

RUN gradle clean shadowJar --no-daemon

#
# RELEASE image
#
FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine AS release

COPY --from=build /cache/build/libs/*all.jar /service.jar

COPY docker-entrypoint.sh /
RUN apk add --no-cache curl && \
    chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
