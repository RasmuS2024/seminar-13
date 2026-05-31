# Cеминар 11 по SysDеsign


## Реализовано (Семинар-11):
- добавлен сервис Kafka в конфиги Docker-compose и в сервисы space-api, telemetry-service

## Запуск

### Требования
- Docker
- Docker Compose

### Перед запуском
Создайте файл `.env` в корне проекта:
```env
DB_NAME=postgres
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
```

или скопируйте и настройте `.env`:
```bash
cp .env.example .env
```

При необходимости настройте порты в docker-compose.yml

Запустите командой:
```docker compose up```

## Описание проекта
Работа со спутниками, их группировками и выполнением миссий.

Проект содержит 3 подпроекта (модуля):
- space-api (основной сервис), запускается на порту 8080
  (после запуска доступен Swagger: http://localhost:8080/swagger-ui/index.html);
- space-scheduler (планировщик), запускается на порту 8081;
- telemetry-service (сервис телеметрии), запускается на порту 9091, сервис gRPC 9090;
  А также сервисы:
- Kafka на порту 9093;
- Kafka UI на порту 8085;
- PostgreSQL на порту 5432.



GitHub Actions:

![Java CI](https://github.com/RasmuS2024/seminar-11/actions/workflows/main.yml/badge.svg)

![Docker Build](https://github.com/RasmuS2024/seminar-11/actions/workflows/deploy.yml/badge.svg)



