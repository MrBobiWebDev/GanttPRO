# Структура проекта GanttPRO

## Созданные файлы

### Конфигурация
- `pom.xml` - Maven конфигурация с зависимостями
- `src/main/resources/application.properties` - Конфигурация Spring Boot
- `.gitignore` - Правила игнорирования файлов

### Java классы

#### Модели (model/)
- `User.java` - Сущность пользователя
- `UserRole.java` - Enum для ролей
- `Project.java` - Сущность проекта
- `Task.java` - Сущность задачи
- `TaskStatus.java` - Enum для статусов задач

#### DTO (dto/)
- `RegisterRequest.java` - DTO для регистрации
- `ProjectForm.java` - Form для создания/редактирования проекта
- `TaskForm.java` - Form для создания/редактирования задачи

#### Репозитории (repository/)
- `UserRepository.java` - JPA репозиторий для User
- `ProjectRepository.java` - JPA репозиторий для Project
- `TaskRepository.java` - JPA репозиторий для Task

#### Сервисы (service/)
- `AuthService.java` - Логика авторизации
- `ProjectService.java` - Логика проектов
- `TaskService.java` - Логика задач

#### Контроллеры (controller/)
- `AuthController.java` - Контроллер для регистрации/входа
- `DashboardController.java` - Контроллер dashboard
- `ProjectController.java` - Контроллер проектов (MVC)
- `TaskController.java` - Контроллер задач (MVC)
- `ApiProjectController.java` - REST API для проектов
- `ApiTaskController.java` - REST API для задач

#### Конфигурация (config/)
- `SecurityConfig.java` - Конфигурация Spring Security
- `WebConfig.java` - Конфигурация Web MVC

#### Главный класс
- `GanttproApplication.java` - Spring Boot приложение с CommandLineRunner для демо-данных

### Шаблоны (templates/)

#### Аутентификация
- `auth/login.html` - Страница входа
- `auth/register.html` - Страница регистрации

#### Проекты
- `project/form.html` - Форма создания/редактирования проекта
- `project/view.html` - Просмотр проекта с диаграммой Ганта

#### Задачи
- `task/form.html` - Форма создания/редактирования задачи

#### Общие
- `dashboard.html` - Dashboard со списком проектов
- `layout.html` - Базовый layout (используется как основа)

### Статические файлы (static/)

#### CSS
- `css/style.css` - Единый файл со всеми стилями приложения

#### JavaScript
- `js/gantt.js` - Утилиты для диаграммы Ганта

## Основные функции

### Авторизация
- BCrypt шифрование паролей
- Spring Security для контроля доступа
- Разделение данных между пользователями

### Проекты
- CRUD операции
- Привязка к пользователю
- Отслеживание дат начала/окончания

### Задачи
- CRUD операции
- Статусы: TODO, IN_PROGRESS, DONE, BLOCKED
- Прогресс от 0 до 100%
- Отслеживание исполнителя

### Диаграмма Ганта
- Визуализация задач как горизонтальных полос
- Цветовая кодировка по статусам
- Отображение прогресса
- Адаптивный размер полос

## Безопасность

✅ Пароли хешируются через BCrypt  
✅ CSRF защита включена  
✅ Пользователи видят только свои данные  
✅ SQL injection защита через параметризованные запросы  
✅ Проверка доступа на уровне контроллеров  

## База данных

По умолчанию: **H2 in-memory**
- Автоматическое создание таблиц
- Демо-данные загружаются при старте
- Консоль доступна по адресу http://localhost:8080/h2-console

## REST API

### Проекты
```
GET    /api/projects              - Список проектов
GET    /api/projects/{id}         - Деталь проекта
POST   /api/projects              - Создание
PUT    /api/projects/{id}         - Обновление
DELETE /api/projects/{id}         - Удаление
```

### Задачи
```
GET    /api/projects/{id}/tasks         - Список задач
GET    /api/projects/{id}/tasks/{id}    - Деталь задачи
POST   /api/projects/{id}/tasks         - Создание
PUT    /api/projects/{id}/tasks/{id}    - Обновление
DELETE /api/projects/{id}/tasks/{id}    - Удаление
```

## Демо учетные данные

Email: demo@example.com
Пароль: demo123

Пользователю автоматически создаются:
- 2 проекта
- 9 задач с разными статусами и прогрессом

## Команды для запуска

```bash
# Установка зависимостей
mvn clean install

# Запуск приложения
mvn spring-boot:run

# Открыть браузер
http://localhost:8080
```

## Что дальше

### Приоритеты второй итерации
1. Улучшение фильтрации и сортировки
2. Поиск по проектам
3. Экспорт отчетов
4. История изменений

### Приоритеты третьей итерации
1. Командная работа
2. Разные роли (VIEWER, EDITOR, OWNER)
3. Уведомления и комментарии
4. Интеграции

## Заметки разработчика

- Все user-facing тексты на русском языке ✅
- Lombok не используется ✅
- Обработка всех основных операций с проектами и задачами ✅
- Безопасность доступа реализована ✅
- Адаптивный дизайн ✅
- Демо-данные для тестирования ✅
