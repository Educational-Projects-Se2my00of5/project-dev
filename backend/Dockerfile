# =========================================================================
# Этап 1: Сборка проекта (Builder)
# Используем официальный образ Maven с JDK 21
# =========================================================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# =========================================================================
# Этап 2: Создание финального, легковесного образа
# Используем официальный легковесный образ с JRE 21
# =========================================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт, который будет слушать наше приложение
EXPOSE 8080

# Команда для запуска приложения при старте контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]