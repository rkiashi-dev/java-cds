# =========================================================
# Stage 1: Build
# =========================================================
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven && \
    mvn -B -q package -DskipTests && \
    mv target/java-cds-*.jar app.jar

# =========================================================
# Stage 2: No-CDS runtime image
# =========================================================
FROM eclipse-temurin:17-jre-jammy AS no-cds
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["-jar", "/app/app.jar"]

# =========================================================
# Stage 3: CDS archive generation
# =========================================================
FROM eclipse-temurin:17-jre-jammy AS cds-generator
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar

RUN java -XX:ArchiveClassesAtExit=application.jsa \
         -Dspring.context.exit=onRefresh \
         -jar app.jar || true

# =========================================================
# Stage 4: With-CDS runtime image
# =========================================================
FROM eclipse-temurin:17-jre-jammy AS with-cds
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar
COPY --from=cds-generator /app/application.jsa application.jsa
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ENV JAVA_OPTS="-XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:SharedArchiveFile=/app/application.jsa"
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["-jar", "/app/app.jar"]
