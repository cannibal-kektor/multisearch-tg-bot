# Multisearch Telegram Bot 🤖🔍

[![Java](https://img.shields.io/badge/Java-red?logo=openjdk&logoColor=white)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=white)](https://mongodb.com)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white)](https://elastic.co/elasticsearch/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?logo=rabbitmq&logoColor=white)](https://rabbitmq.com)
[![Apache PDFBox](https://img.shields.io/badge/Apache_PDFBox-D22128?logo=apachepdfbox&logoColor=white)](https://pdfbox.apache.org)
[![Apache POI](https://img.shields.io/badge/Apache_POI-21759B?logo=apacheopenoffice&logoColor=white)](https://poi.apache.org)
[![Jsoup](https://img.shields.io/badge/Jsoup-1E6C3F?logo=scrapinghub&logoColor=white)](https://jsoup.org)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white)](https://elastic.co/elasticsearch/)
[![Telegram Bot API](https://img.shields.io/badge/Telegram_Bot_API-0088CC?logo=telegram&logoColor=white)](https://core.telegram.org/bots/api)
[![Kibana](https://img.shields.io/badge/Kibana-005571?logo=kibana&logoColor=white)](https://elastic.co/kibana/)

Telegram-бот для полнотекстового поиска по документам с системой управления пользователями и микросервисной
архитектурой.

## 🌟 Основные возможности

- **📥 Загрузка документов**: Поддержка PDF, DOCX файлов и HTML-страниц по URL
- **🔍 Интеллектуальный поиск**: Полнотекстовый поиск по содержимому с ранжированием результатов
- **👥 Управление пользователями**: Ролевая система (владелец/администраторы/пользователи)
- **🌍 Мультиязычный интерфейс**: Русский и английский языки
- **⚡ Распределённая архитектура**: Два микросервиса на Spring Boot
- **📊 Комплексный мониторинг**: Интеграция с Elastic Stack и Kibana

## 🚀 Команды бота

### 👤 Для всех пользователей

| Команда      | Описание                                                                 |
|--------------|--------------------------------------------------------------------------|
| `/start`     | Начало работы с ботом (создаёт заявку на регистрацию при первом запуске) |
| `/info`      | Основная информация о боте                                               |
| `/commands`  | Список доступных команд                                                  |
| `/upload`    | Загрузка документа (PDF/DOCX) или HTML-страницы по URL                   |
| `/search`    | Поиск по всем загруженным документам                                     |
| `/documents` | Список ваших документов                                                  |
| `/delete`    | Удаление документа по имени                                              |
| `/contents`  | Просмотр глав документа (для PDF/DOCX) или содержимого HTML-страницы     |
| `/chapter`   | Просмотр содержимого главы по ID                                         |
| `/language`  | Смена языка интерфейса (EN/RU)                                           |

### ⚙️ Для администраторов

| Команда                  | Описание                                     |
|--------------------------|----------------------------------------------|
| `/ban`                   | Блокировка пользователя (по username или ID) |
| `/unban`                 | Разблокировка пользователя                   |
| `/registration_requests` | Просмотр заявок на регистрацию               |
| `/register`              | Одобрение заявки пользователя                |
| `/users`                 | Статистика по пользователям и их документам  |

### 👑 Только для владельца

| Команда    | Описание                                                             |
|------------|----------------------------------------------------------------------|
| `/promote` | Назначение администратора                                            |
| `/demote`  | Снятие прав администратора                                           |
| `/purge`   | Удаление всех документов пользователя                                |
| `/logging` | Изменение уровня логирования (TRACE/DEBUG/INFO/WARN/ERROR/FATAL/OFF) |

## 🧩 Техническая архитектура

### Микросервисная структура

Система разделена на два независимых сервиса:

1. **Сервис Bot**:
    - Регистрирует бот в Telegram API
    - Принимает сообщения от Telegram API
    - Первичная обработка команд
    - Составление задачи из команды
    - Отправляет задачи в RabbitMQ
    - Проверка прав
   
2. **Сервис Processor**:
    - Получает задачи из RabbitMQ
    - Выполняет бизнес-логику команд
    - Работает с базами данных
    - Отправляет ответ пользователям

### Поток данных

1. Пользователь отправляет команду в Telegram
2. Bot получает команду
3. Bot составляет задачу из команды и отправляет ее в RabbitMQ
4. Processor получает задачу из RabbitMQ
5. Processor выполняет задачу (логику команды)
6. Processor взаимодействует с базами данных (Mongo и Elastic)
7. Processor отправляет результат пользователю

### Компоненты системы и порты

| Компонент             | Порт        | Назначение                                                                                              |
|-----------------------|-------------|---------------------------------------------------------------------------------------------------------|
| **Bot service**       | -           | Регистрация бота, получение, первичная обработка telegram сообщений                                     |
| **Processor service** | -           | Выполнение бизнес-логики команд, отправка ответа пользователю                                           |
| **MongoDB**           | 27017       | Хранение данных пользователей, метаданных документов, заявок на регистрацию                             |
| **Elasticsearch**     | 9200        | Хранение глав документов и их содержимого, полнотекстовый поиск, хранение метрик и логов всех компонент |
| **RabbitMQ**          | 5671, 15671 | Обеспечение взаимодействия между сервисами                                                              |
| **Kibana**            | 5601        | Визуализация метрик и логов, мониторинг работы сервисов                                                 |
| **Elastic Agent**     |             | Сбор логов и метрик сервисов с последующей отправкой в Elasticsearch                                    |

## 🛠️ Стек технологий

### Используемые фреймворки

- **База:** \
  [![Java](https://img.shields.io/badge/Java-red?logo=openjdk&logoColor=white)](https://java.com)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
- **Взаимодействие с базами данных:** \
  [![MongoDB](https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=white)](https://mongodb.com)
  [![Spring Data MongoDB](https://img.shields.io/badge/Spring_Data_MongoDB-47A248?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-mongodb)\
  [![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white)](https://elastic.co/elasticsearch/)
  [![Spring Data Elasticsearch](https://img.shields.io/badge/Spring_Data_Elasticsearch-47A248?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-elasticsearch)
- **Взаимодействие с брокером:** \
  [![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?logo=rabbitmq&logoColor=white)](https://rabbitmq.com)
  [![Spring AMQP](https://img.shields.io/badge/Spring_AMQP-47A248?logo=spring&logoColor=white)](https://spring.io/projects/spring-amqp)
- **Парсинг документов:**
    - ***pdf***\
      [![Apache PDFBox](https://img.shields.io/badge/Apache_PDFBox-D22128?logo=apachepdfbox&logoColor=white)](https://pdfbox.apache.org)
    - ***docx***\
      [![Apache POI](https://img.shields.io/badge/Apache_POI-21759B?logo=apacheopenoffice&logoColor=white)](https://poi.apache.org)
    - ***html***\
      [![Jsoup](https://img.shields.io/badge/Jsoup-1E6C3F?logo=scrapinghub&logoColor=white)](https://jsoup.org)
- **Функции полнотекстового поиска:** \
  [![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white)](https://elastic.co/elasticsearch/)
  [![Spring Data Elasticsearch](https://img.shields.io/badge/Spring_Data_Elasticsearch-47A248?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-elasticsearch)
- **Взаимодействие с Telegram:**\
  [![Telegram Bot API](https://img.shields.io/badge/Telegram_Bot_API-0088CC?logo=telegram&logoColor=white)](https://core.telegram.org/bots/api)
  [![TelegramBots Repo](https://img.shields.io/badge/GitHub_Repo-TelegramBots-181717?logo=github)](https://github.com/rubenlagus/TelegramBots)
- **Мониторинг:**\
  [![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white)](https://elastic.co/elasticsearch/)
  [![Kibana](https://img.shields.io/badge/Kibana-005571?logo=kibana&logoColor=white)](https://elastic.co/kibana/)
  [![Elastic Agent](https://img.shields.io/badge/Elastic_Agent-005571?logo=elastic-stack&logoColor=white)](https://elastic.co/elastic-agent/)
- **Инфраструктура:**\
  [![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)](https://docker.com)
  [![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
- **Дополнительные фреймворки**
    - **Сбор метрик:**\
      [![Spring Actuator](https://img.shields.io/badge/Spring_Actuator-6DB33F?logo=springboot&logoColor=white)](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
      [![Micrometer](https://img.shields.io/badge/Micrometer-FF4F8B?logo=micrometer&logoColor=white)](https://micrometer.io)
    - **Валидация данных:**\
      [![Spring Validation](https://img.shields.io/badge/Spring_Validation-6DB33F?logo=spring&logoColor=white)](https://spring.io/guides/gs/validating-form-input/)
    - **Логирование:**\
      [![SLF4J](https://img.shields.io/badge/SLF4J-1C1C1C?logo=slf4j&logoColor=white)](https://www.slf4j.org)
      [![Logback](https://img.shields.io/badge/Logback-00B800?logo=logback&logoColor=white)](https://logback.qos.ch)
      [![Spring AOP](https://img.shields.io/badge/Spring_AOP-6DB33F?logo=spring&logoColor=white)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)
      [![ECS](https://img.shields.io/badge/ECS-005571?logo=elastic-stack&logoColor=white)](https://www.elastic.co/guide/en/ecs/current/index.html)
    - **Генерация кода:**\
      [![Lombok](https://img.shields.io/badge/Lombok-FF0040?logo=lombok&logoColor=white)](https://projectlombok.org)
      [![MapStruct](https://img.shields.io/badge/MapStruct-3F7FBF?logo=mapstruct&logoColor=white)](https://mapstruct.org)
    -  **Сборка:**\
       [![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org)

## ⚙️ Конфигурация

Замените в папке все `.secret.example` файлы на `.secret`.
Обязательно нужно установить(не содержит значений по умолчанию) username бота, token бота и свой telegram id в
bot.secret файле.

```ini
# Bot username
bot.username=
# Bot token
bot.token=
# Bot creator telegram id
bot.creator-id=
# Elastic user for metrics export
elastic.username=elastic
# Elastic user password for metrics export
elastic.password=testPassword
```

## 🐳 Запуск проекта

### Требования

- Docker
- Docker Compose

### Пошаговая инструкция

```bash
# 1. Клонирование репозитория
git clone https://github.com/your-username/multisearch-bot.git
cd multisearch-bot

# 2. Настройка секретов
cp secret/bot.secret.example secret/bot.secret
cp secret/elastic.secret.example secret/elastic.secret
cp secret/rabbit.secret.example secret/rabbit.secret
cp secret/mongo.secret.example secret/mongo.secret
cp secret/kibana.secret.example secret/kibana.secret

# Отредактируйте bot.secret файл, установив Telegram bot username,
# Telegram Bot Token и Telegram Id root акканута 
vim secret/bot.secret

# 3. Запуск системы
docker compose up -d

# 4. Проверка работы
docker compose ps
```

## 📊 Логирование и мониторинг

Система использует Elastic Stack для комплексного мониторинга:

### Сбор данных

- **Логи**: Все компоненты системы отправляют логи в стандартный вывод(stdout) в ECS формате
- **Elastic Agent**: Автоматически собирает логи из контейнеров Docker и отправляет их в Elasticsearch
- **Метрики**: Spring Actuator + Micrometer собирают детальные метрики работы приложений

### Визуализация в Kibana

- **Мониторинг Логов Компонентов**: Возможность интерактивно просматривать, фильтровать логи компонентов
- **Мониторинг Метрик Компонентов**: Готовые dashboards для компонентов системы, позволяющие визуализировать метрики в
  реальном времени
- **Кастомизация**: Возможность создавать свои dashboard и настраивать Elastic Agent

## ⚠️ Известные ограничения

1. **HTML-документы**: Обрабатываются как единая глава без разделения на разделы
2. **Размер файлов**: Максимальный размер документа ограничен 20 МБ
3. **Форматы**: Поддерживаются только PDF, DOCX и HTML-файлы
4. **Производительность**: Для больших объемов данных требуется дополнительная настройка ресурсов Elasticsearch

## 💡 Особенности реализации

### Обработка документов

**PDF-файлы**:

- Автоматическое разбиение на главы по структуре документа
- Извлечение текстового контента
- Сохранение иерархических связей между разделами
- Хранение метаданных (автор, дата создания) в MongoDB

**DOCX-файлы**:

- Анализ стилей заголовков для определения структуры документа
- Извлечение текстового контента
- Сохранение иерархических связей между разделами
- Хранение метаданных (автор, дата создания) в MongoDB

**HTML-страницы**:

- Обработка всего контента как единого блока
- Автоматическая очистка от HTML-тегов и скриптов

### 🔐 Безопасность

- **Телеграм роли**: Четкое разделение прав по ролям (владелец/админ/пользователь)
- **Валидация входных данных**: Проверка всех входящих запросов и параметров
- **Изоляция данных**: Разграничение доступа к документам пользователей
- **HTTPS-шифрование**: Защита внутренней коммуникации между компонентами
- **Генерация сертификатов**: Для настройки https на всех компонентах требуются сертификаты, которые генерируются
  отдельным сервисом и потом монтируются для использования в компоненты
- **Защита учетных данных**: Использование docker compose **secret** элемента для хранения чувствительной информации в
  `.secret` файлах 