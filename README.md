# Dokumentacja Techniczna - URL Shortener

## 1. Opis projektu

### Cel
URL Shortener to rozproszona aplikacja mikroserwisowa umożliwiająca skracanie długich adresów URL oraz ich przekierowania. System został zaprojektowany z uwzględnieniem bezpieczeństwa, skalowalności i niezawodności.

### Ogólny przegląd
Aplikacja składa się z trzech głównych mikroserwisów:
- **Shortener Service** (port 8081) - generowanie skróconych URL-i
- **Redirect Service** (port 8082) - obsługa przekierowań
- **Cleanup Service** (port 8083) - automatyczne czyszczenie wygasłych URL-i

### Zastosowanie
- Skracanie długich URL-i dla mediów społecznościowych
- Śledzenie kliknięć i analityka
- Zarządzanie czasem życia linków
- Filtrowanie niebezpiecznych lub niechcianych treści

### Kluczowe funkcjonalności
- Automatyczne wygaśnięcie URL-i po określonym czasie (domyślnie 3 minuty)
- Filtrowanie słów zakazanych z alertami Kafka
- Blokowanie niebezpiecznych URL-i w czasie rzeczywistym
- Automatyczne czyszczenie bazy danych
- Zarządzanie strefą czasową (Europe/Warsaw)

---

## 2. Technologie i zależności

### Backend
- **Java 17** - główny język programowania
- **Spring Boot 3.x** - framework aplikacyjny
- **Spring Data Cassandra** - integracja z bazą danych
- **Spring Kafka** - obsługa komunikacji asynchronicznej
- **Spring Scheduling** - zadania cykliczne

### Baza danych
- **Apache Cassandra 3.11.8** - rozproszona baza danych NoSQL

### Message Broker
- **Apache Kafka** - komunikacja między serwisami
- **Zookeeper** - koordynacja klastra Kafka

### Konteneryzacja
- **Docker** - konteneryzacja aplikacji
- **Docker Compose** - orkiestracja środowiska

### Biblioteki pomocnicze
- **SLF4J + Logback** - logowanie
- **Jackson** - serializacja JSON
- **Maven/Gradle** - zarządzanie zależnościami

---

## 3. Wymagania systemowe

### Minimalne wymagania sprzętowe
- **RAM**: 4 GB (8 GB zalecane)
- **CPU**: 2 rdzenie (4 rdzenie zalecane)
- **Dysk**: 10 GB wolnego miejsca
- **Sieć**: Dostęp do internetu dla pobierania obrazów Docker

### Wymagania programowe
- **Docker Engine**: 20.0+
- **Docker Compose**: 2.0+
- **Java JDK**: 17+ (dla lokalnego developmentu)
- **Maven/Gradle**: 3.6+/7.0+ (dla lokalnego buildowania)

### Porty sieciowe
Aplikacja wymaga dostępu do następujących portów:
- `8081` - Shortener Service
- `8082` - Redirect Service  
- `8083` - Cleanup Service
- `9042` - Cassandra
- `9092` - Kafka
- `2181` - Zookeeper

---

## 4. Instrukcja instalacji

### Instalacja lokalna z Docker Compose

1. **Klonowanie repozytorium**
```bash
git clone <repository-url>
cd url-shortener
```

2. **Budowanie aplikacji Java**
```bash
# Dla każdego serwisu
cd shorturl2
./gradlew build
cd ../read_redirect
./gradlew build
cd ../cleanup_service
./gradlew build
cd ..
```

3. **Uruchomienie środowiska**
```bash
docker-compose up --build
```

4. **Weryfikacja instalacji**
```bash
# Sprawdź status kontenerów
docker-compose ps

# Sprawdź logi
docker-compose logs -f shortener-service
```

### Instalacja produkcyjna

1. **Przygotowanie środowiska**
```bash
# Utwórz dedykowanego użytkownika
sudo useradd -m -s /bin/bash urlshortener
sudo usermod -aG docker urlshortener

# Przygotuj katalogi
sudo mkdir -p /opt/urlshortener
sudo chown urlshortener:urlshortener /opt/urlshortener
```

2. **Konfiguracja produkcyjna**
```bash
# Skopiuj pliki konfiguracyjne
cp docker-compose.yml /opt/urlshortener/
cp -r . /opt/urlshortener/

# Dostosuj konfigurację produkcyjną
nano /opt/urlshortener/docker-compose.yml
```

3. **Uruchomienie w trybie produkcyjnym**
```bash
cd /opt/urlshortener
docker-compose -f docker-compose.yml up -d
```

---

## 5. Konfiguracja

### Zmienne środowiskowe

#### Shortener Service
```properties
TZ=Europe/Warsaw
SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra
SPRING_DATA_CASSANDRA_KEYSPACE_NAME=redirect_keyspace
SPRING_DATA_CASSANDRA_LOCAL_DATACENTER=datacenter1
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

#### Redirect Service
```properties
TZ=Europe/Warsaw
SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra
SPRING_DATA_CASSANDRA_KEYSPACE_NAME=redirect_keyspace
SPRING_DATA_CASSANDRA_LOCAL_DATACENTER=datacenter1
```

#### Cleanup Service
```properties
TZ=Europe/Warsaw
SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra
SPRING_DATA_CASSANDRA_KEYSPACE_NAME=redirect_keyspace
SPRING_DATA_CASSANDRA_LOCAL_DATACENTER=datacenter1
```

### Pliki konfiguracyjne

#### application.properties - Shortener Service
```properties
server.port=8081
short.url.ttl.seconds=180
forbidden.words.kafka.topic=forbidden-words-topic
kafka.enabled=true

# Cassandra
spring.data.cassandra.contact-points=cassandra
spring.data.cassandra.keyspace-name=redirect_keyspace
spring.data.cassandra.local-datacenter=datacenter1

# Kafka
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=url-shortener-group
```

#### application.properties - Cleanup Service
```properties
server.port=8083
cleanup.enabled=true
cleanup.strategy=EXPIRATION_TIME
cleanup.max-age=3
cleanup.inactive=3
cleanup.schedule=0 * * * * ?
```

### Konfiguracja Docker Compose

Główne parametry w `docker-compose.yml`:
- **Healthchecks** - monitorowanie stanu serwisów
- **Dependencies** - kolejność uruchamiania
- **Volume mounts** - synchronizacja czasu systemowego
- **Environment variables** - konfiguracja runtime

---

## 6. Struktura projektu

```
url-shortener/
├── docker-compose.yml              # Orkiestracja kontenerów
├── shorturl2/                      # Serwis skracania URL-i
│   ├── Dockerfile
│   ├── src/main/java/com/example/
│   │   ├── ProjectApplication.java
│   │   ├── CassandraConfig.java
│   │   ├── KafkaConfig.java
│   │   ├── controller/
│   │   │   ├── ShortUrlController.java
│   │   │   └── ForbiddenWordsController.java
│   │   ├── service/
│   │   │   ├── ShortUrlService.java
│   │   │   ├── ForbiddenWordService.java
│   │   │   └── SimpleBlockedUrlListener.java
│   │   ├── model/
│   │   │   └── ShortUrlEntity.java
│   │   └── repository/
│   │       └── ShortUrlRepository.java
│   └── src/main/resources/
│       └── application.properties
├── read_redirect/                  # Serwis przekierowań
│   ├── Dockerfile
│   ├── src/main/java/com/example/project/
│   │   ├── RedirectApplication.java
│   │   ├── config/
│   │   │   └── CassandraSettings.java
│   │   ├── controller/
│   │   │   └── RedirectController.java
│   │   ├── service/
│   │   │   └── RedirectService.java
│   │   ├── model/
│   │   │   └── ShortUrlEntity.java
│   │   └── repository/
│   │       └── ShortUrlRepository.java
│   └── src/main/resources/
│       └── application.properties
└── cleanup_service/                # Serwis czyszczenia
    ├── Dockerfile
    ├── src/main/java/com/example/cleanup/
    │   ├── CleanupApplication.java
    │   ├── config/
    │   │   └── CassandraConfig.java
    │   ├── scheduler/
    │   │   └── CleanupScheduler.java
    │   ├── service/
    │   │   └── CleanupService.java
    │   ├── model/
    │   │   └── ShortUrlEntity.java
    │   └── repository/
    │       └── ShortUrlRepository.java
    └── src/main/resources/
        └── application.properties
```

### Opis głównych komponentów

#### Shortener Service
- **ShortUrlController** - REST API dla skracania URL-i
- **ForbiddenWordsController** - zarządzanie słowami zakazanymi
- **ShortUrlService** - logika biznesowa skracania
- **ForbiddenWordService** - detekcja i alerting słów zakazanych
- **SimpleBlockedUrlListener** - nasłuchiwanie blokowanych URL-i z Kafka

#### Redirect Service  
- **RedirectController** - obsługa przekierowań HTTP
- **RedirectService** - logika biznesowa przekierowań

#### Cleanup Service
- **CleanupScheduler** - harmonogram czyszczenia (cron)
- **CleanupService** - logika usuwania wygasłych URL-i

---

## 7. Użytkowanie

### Uruchomienie aplikacji

1. **Start wszystkich serwisów**
```bash
docker-compose up -d
```

2. **Weryfikacja statusu**
```bash
docker-compose ps
docker-compose logs -f
```

3. **Zatrzymanie aplikacji**
```bash
docker-compose down
```

### Podstawowe operacje

#### Skrócenie URL-a
```bash
curl -X POST http://localhost:8081/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com/very/long/url/path"}'
```

Odpowiedź:
```json
{
  "shortUrl": "http://localhost:8081/Abc123"
}
```

#### Przekierowanie
```bash
curl -I http://localhost:8082/Abc123
```

#### Zarządzanie słowami zakazanymi
```bash
# Pobranie listy
curl http://localhost:8081/admin/forbidden-words

# Dodanie nowego słowa
curl -X POST http://localhost:8081/admin/forbidden-words \
  -H "Content-Type: application/json" \
  -d '{"word": "spam"}'

# Usunięcie słowa
curl -X DELETE http://localhost:8081/admin/forbidden-words/spam
```

### Monitorowanie

#### Logi aplikacji
```bash
# Wszystkie serwisy
docker-compose logs -f

# Konkretny serwis
docker-compose logs -f shortener-service
docker-compose logs -f cleanup-service
```

#### Status bazy danych
```bash
# Połączenie z Cassandra
docker exec -it cassandra cqlsh

# Sprawdzenie danych
USE redirect_keyspace;
SELECT * FROM short_url_entity LIMIT 10;
```

#### Kafka monitorowanie
```bash
# Lista tematów
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Nasłuchiwanie wiadomości
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic forbidden-words-topic \
  --from-beginning
```

---

## 8. API / Interfejsy

### Shortener Service API (port 8081)

#### POST /shorten
Tworzy skrócony URL.

**Request:**
```json
{
  "url": "https://www.example.com"
}
```

**Response Success (200):**
```json
{
  "shortUrl": "http://localhost:8081/Abc123"
}
```

**Response Error (400):**
```json
{
  "error": "URL zawiera zabronione słowo: spam"
}
```

#### GET /admin/forbidden-words
Pobiera listę słów zakazanych.

**Response:**
```json
["spam", "phishing", "malware", "onet"]
```

#### POST /admin/forbidden-words
Dodaje nowe słowo zakazane.

**Request:**
```json
{
  "word": "newbadword"
}
```

**Response:**
```json
{
  "message": "Word added successfully",
  "word": "newbadword"
}
```

#### DELETE /admin/forbidden-words/{word}
Usuwa słowo zakazane.

**Response:**
```json
{
  "message": "Word removed successfully",
  "word": "spam"
}
```

### Redirect Service API (port 8082)

#### GET /{shortKey}
Przekierowuje na oryginalny URL.

**Response Success (302):**
```
Location: https://www.example.com
```

**Response Error (404):**
URL nie istnieje, wygasł lub został zablokowany.

### Cleanup Service (port 8083)

Serwis nie udostępnia publicznego API. Działanie oparte na harmonogramie cron.

### Komunikacja Kafka

#### Topic: forbidden-words-topic
Struktura wiadomości o wykrytych słowach zakazanych:
```json
{
  "timestamp": "2025-06-19T10:30:00",
  "url": "http://spam-site.com",
  "forbiddenWord": "spam",
  "eventType": "FORBIDDEN_WORD_DETECTED"
}
```

#### Topic: blocked-urls
Struktura wiadomości o blokowanych URL-ach:
```
BLOCKED: http://malicious-site.com
```

### Schemat bazy danych Cassandra

#### Tabela: short_url_entity
```sql
CREATE TABLE short_url_entity (
    short_key text PRIMARY KEY,
    original_url text,
    expiration_time bigint,
    creation_time bigint,
    last_access_time bigint
);
```

**Pola:**
- `short_key` - unikalny identyfikator skróconego URL-a
- `original_url` - oryginalny długi URL
- `expiration_time` - timestamp wygaśnięcia (milliseconds)
- `creation_time` - timestamp utworzenia (milliseconds)  
- `last_access_time` - timestamp ostatniego dostępu (milliseconds)

---
