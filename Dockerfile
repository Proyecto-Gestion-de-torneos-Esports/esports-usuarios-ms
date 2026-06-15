# ETAPA 1: Compilar con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
# Cache de dependencias (esta capa solo se regenera si cambiapom.xml)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ETAPA 2: Solo el runtime (imagen final mas pequeña)

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiar SOLO el JAR desde la etapa builder
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]

