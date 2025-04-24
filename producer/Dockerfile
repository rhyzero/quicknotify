# -------- build stage --------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .                      
RUN mvn -q -pl producer -am package -DskipTests
    
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/producer/target/producer-*.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
    