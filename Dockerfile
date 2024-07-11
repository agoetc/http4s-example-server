FROM eclipse-temurin:17.0.10_7-jdk-jammy

COPY http-server/target/pack /srv/httpServer

# Using a non-privileged user:
USER nobody
WORKDIR /srv/httpServer

ENTRYPOINT ["sh", "./bin/httpServer"]
