# GanttPRO - Простой менеджер проектов с диаграммой Ганта

Веб-приложение для управления проектами и задачами с визуализацией в виде диаграммы Ганта.

## Технологический стек

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring MVC**
- **Spring Security**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL / H2**
- **Thymeleaf**
- **HTML/CSS/JavaScript**

## Возможности (MVP)

✅ Регистрация и авторизация пользователей  
✅ Управление проектами (CRUD)  
✅ Управление задачами (CRUD)  
✅ Диаграмма Ганта для визуализации задач  
✅ Отслеживание прогресса задач  
✅ Разные статусы задач (Запланирована, В работе, Выполнена, Заблокирована)  
✅ Безопасность и изоляция данных пользователей  

## Структура проекта

```
GanttPRO/
├── src/
│   ├── main/
│   │   ├── java/com/ganttpro/
│   │   │   ├── controller/          # Веб-контроллеры
│   │   │   ├── service/             # Бизнес-логика
│   │   │   ├── repository/          # JPA репозитории
│   │   │   ├── model/               # JPA сущности
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── config/              # Конфигурация (Security)
│   │   │   └── GanttproApplication.java
│   │   ├── resources/
│   │   │   ├── templates/           # Thymeleaf шаблоны
│   │   │   ├── static/
│   │   │   │   ├── css/             # Стили
│   │   │   │   └── js/              # JavaScript
│   │   │   └── application.properties
│   └── test/java/
├── pom.xml
└── README.md
```

## Установка и запуск

### Требования

- Java 17 или выше
- Maven 3.6 или выше
- PostgreSQL 12+ (опционально, по умолчанию используется H2)

### Шаги установки

1. **Клонировать репозиторий**
   ```bash
   git clone <repository-url>
   cd GanttPRO
   ```

2. **Установить зависимости**
   ```bash
   mvn clean install
   ```

3. **Запустить приложение**
   ```bash
   mvn spring-boot:run
   ```

4. **Открыть в браузере**
   ```
   http://localhost:8080
   ```

## Учетные данные для тестирования

При первом запуске приложение автоматически создаст тестового пользователя:

- **Email:** demo@example.com
- **Пароль:** demo123

Пользователь также получит несколько демо-проектов с задачами для проверки функциональности диаграммы Ганта.

## Маршруты приложения

### Публичные страницы
- `GET /` - Главная страница (редирект на dashboard)
- `GET /login` - Страница входа
- `GET /register` - Страница регистрации
- `POST /register` - Отправка формы регистрации

### Защищенные страницы
- `GET /dashboard` - Dashboard с проектами
- `GET /projects/new` - Форма создания проекта
- `GET /projects/{id}` - Просмотр проекта с диаграммой Ганта
- `GET /projects/{id}/edit` - Редактирование проекта
- `POST /projects/{id}/delete` - Удаление проекта

### Управление задачами
- `GET /projects/{projectId}/tasks/new` - Форма создания задачи
- `GET /projects/{projectId}/tasks/{taskId}/edit` - Редактирование задачи
- `POST /projects/{projectId}/tasks/{taskId}/delete` - Удаление задачи

### REST API
- `GET /api/projects` - Список проектов пользователя
- `GET /api/projects/{id}` - Детали проекта
- `POST /api/projects` - Создание проекта
- `PUT /api/projects/{id}` - Обновление проекта
- `DELETE /api/projects/{id}` - Удаление проекта

- `GET /api/projects/{projectId}/tasks` - Список задач проекта
- `GET /api/projects/{projectId}/tasks/{id}` - Детали задачи
- `POST /api/projects/{projectId}/tasks` - Создание задачи
- `PUT /api/projects/{projectId}/tasks/{id}` - Обновление задачи
- `DELETE /api/projects/{projectId}/tasks/{id}` - Удаление задачи

## Модели данных

### User
- id
- name - Имя пользователя
- email - Email (уникальный)
- password - Пароль (хешировано через BCrypt)
- role - Роль (USER, ADMIN)
- createdAt - Дата создания

### Project
- id
- name - Название проекта
- description - Описание
- startDate - Дата начала
- endDate - Дата окончания
- owner - Владелец (User)
- tasks - Список задач
- createdAt - Дата создания

### Task
- id
- title - Название задачи
- description - Описание
- startDate - Дата начала
- endDate - Дата окончания
- status - Статус (TODO, IN_PROGRESS, DONE, BLOCKED)
- progress - Прогресс (0-100%)
- assigneeName - Имя исполнителя
- project - Проект (Project)
- createdAt - Дата создания

## Безопасность

- Пароли хранятся в зашифрованном виде (BCrypt)
- Пользователи видят только свои проекты
- CSRF защита включена
- Spring Security конфигурирует доступ к ресурсам
- SQL Injection защита через параметризованные запросы

## База данных

По умолчанию используется **H2 in-memory database**, которая автоматически создает таблицы при старте.

### Переключение на PostgreSQL

Отредактировать `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ganttpro
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## Функциональность диаграммы Ганта

- **Визуализация:** Каждая задача отображается как горизонтальная полоса
- **Позиционирование:** Положение зависит от startDate относительно начала проекта
- **Длина полосы:** Определяется длительностью задачи (endDate - startDate)
- **Цветовая кодировка:** Разные цвета для разных статусов
- **Прогресс:** Отображается процент выполнения внутри полосы
- **Масштаб:** По умолчанию один день = 32px

## Что не включено в MVP

❌ Drag-and-drop  
❌ Зависимости между задачами  
❌ Командная работа  
❌ Различные роли пользователей (кроме основных)  
❌ Экспорт в PDF/Excel  
❌ Уведомления  
❌ Комментарии  
❌ Загрузка файлов  

## План развития

### Итерация 2
- Улучшенная фильтрация и сортировка
- Поиск по проектам и задачам
- Экспорт отчетов
- История изменений

### Итерация 3
- Командная работа
- Разные роли пользователей (VIEWER, EDITOR, OWNER)
- Уведомления и комментарии
- Интеграции с календарем

## Разработка и отладка

### H2 Console

При работе с H2 базой данных можно открыть консоль:
```
http://localhost:8080/h2-console
```

Параметры подключения используют стандартные значения из `application.properties`.

### Логирование

Уровень логирования можно изменить в `application.properties`:
```properties
logging.level.com.ganttpro=DEBUG
```

## Возможные ошибки и решения

### Ошибка: "mvn command not found"
Убедитесь, что Maven установлен и добавлен в PATH.

### Ошибка компиляции в Windows
Используйте полные пути в кавычках для директорий с кириллицей.

### Проблемы с подключением к БД
Проверьте параметры подключения в `application.properties` и убедитесь, что база данных запущена.

## Лицензия

MIT License

## Автор

GanttPRO MVP - 2026
