# =========================================================
# Stage 1: Build
# =========================================================
FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -q package -DskipTests && \
    mv target/java-cds-*.jar app.jar

# =========================================================
# Stage 2: No-CDS runtime image
# =========================================================
FROM eclipse-temurin:17-jre-jammy AS no-cds
RUN groupadd --system app && useradd --system --gid app --home-dir /app --create-home --shell /usr/sbin/nologin app
WORKDIR /app
COPY --chown=app:app --from=builder /workspace/app.jar app.jar
COPY --chown=app:app docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
USER app
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
RUN groupadd --system app && useradd --system --gid app --home-dir /app --create-home --shell /usr/sbin/nologin app
WORKDIR /app
COPY --chown=app:app --from=builder /workspace/app.jar app.jar
COPY --chown=app:app --from=cds-generator /app/application.jsa application.jsa
COPY --chown=app:app docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ENV JAVA_OPTS="-XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:SharedArchiveFile=/app/application.jsa"
USER app
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["-jar", "/app/app.jar"]
