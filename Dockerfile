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
ENTRYPOINT ["java", "-jar", "app.jar"]

# =========================================================
# Stage 3: CDS archive generation
# =========================================================
FROM eclipse-temurin:17-jre-jammy AS cds-generator
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar

# Generate CDS archive (ArchiveClassesAtExit requires a JDK or supported JRE)
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

ENTRYPOINT ["java", \
            "-XX:SharedArchiveFile=application.jsa", \
            "-jar", "app.jar"]
