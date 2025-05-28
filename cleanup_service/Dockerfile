FROM openjdk:17-jdk-slim

# 1. Ustawiamy zmienną środowiskową z Twoją strefą
ENV TZ=Europe/Warsaw \
    JAVA_TOOL_OPTIONS=-Duser.timezone=Europe/Warsaw

# 2. Instalujemy tzdata, konfigurujemy lokalny czas, sprzątamy cache apt
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
