# Використовуємо офіційний легкий образ Java 17
FROM eclipse-temurin:17-jdk-alpine

# Створюємо робочу директорію всередині контейнера
WORKDIR /app

# Копіюємо скомпільований .jar файл у контейнер
COPY target/*.jar app.jar

# Вказуємо команду, яка запустить наш Spring Boot застосунок
ENTRYPOINT ["java", "-jar", "app.jar"]