# ✅ Проверка сборки проекта GanttPRO

Этот документ описывает как проверить, что проект собирается и работает корректно.

## Быстрая проверка (5 минут)

### На Windows:

Откройте Command Prompt в папке проекта и выполните:

```batch
@echo off
echo Проверка сборки проекта GanttPRO...
echo.

echo Шаг 1: Очистка проекта...
mvnw.cmd clean

echo Шаг 2: Загрузка зависимостей...
mvnw.cmd install -DskipTests

echo Шаг 3: Проверка компиляции...
mvnw.cmd compile

echo.
echo ========================================
echo Сборка проекта завершена!
echo ========================================
echo.
pause
```

Или просто используйте готовый скрипт:
```bash
run-tests.cmd
```

### На Linux/Mac:

Откройте Terminal в папке проекта и выполните:

```bash
#!/bin/bash
echo "Проверка сборки проекта GanttPRO..."
echo ""

echo "Шаг 1: Очистка проекта..."
./mvnw clean

echo "Шаг 2: Загрузка зависимостей..."
./mvnw install -DskipTests

echo "Шаг 3: Проверка компиляции..."
./mvnw compile

echo ""
echo "========================================"
echo "Сборка проекта завершена!"
echo "========================================"
echo ""
```

Или просто используйте готовый скрипт:
```bash
./run-tests.sh
```

## Полная проверка (10-15 минут)

### Выполните все тесты:

**Windows:**
```bash
mvnw.cmd clean test
```

**Linux/Mac:**
```bash
./mvnw clean test
```

**Ожидаемый результат:**
```
[INFO] Running com.ganttpro.GanttproApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.ganttpro.service.AuthServiceTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.ganttpro.service.ProjectServiceTests
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.ganttpro.service.TaskServiceTests
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0

[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
```

## Запуск приложения и проверка в браузере

### Запустите приложение:

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Ожидаемый вывод в консоли:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2026-06-22T10:30:45.123+00:00  INFO 12345 --- [main] com.ganttpro.GanttproApplication        : Starting GanttproApplication using Java 17.0.1
...
2026-06-22T10:30:48.456+00:00  INFO 12345 --- [main] com.ganttpro.GanttproApplication        : Started GanttproApplication in 3.333 seconds (JVM running for 3.789)
```

### Проверьте в браузере:

1. Откройте http://localhost:8080
2. Вы должны увидеть страницу входа
3. Вводите демо учетные данные:
   - Email: `demo@example.com`
   - Пароль: `demo123`
4. После входа вы должны увидеть Dashboard с проектами

## Проверочный список компилирующихся файлов

Все эти файлы должны скомпилироваться без ошибок:

### Java классы
- ✅ `src/main/java/com/ganttpro/GanttproApplication.java`
- ✅ `src/main/java/com/ganttpro/model/*.java` (5 файлов)
- ✅ `src/main/java/com/ganttpro/dto/*.java` (3 файла)
- ✅ `src/main/java/com/ganttpro/repository/*.java` (3 файла)
- ✅ `src/main/java/com/ganttpro/service/*.java` (3 файла)
- ✅ `src/main/java/com/ganttpro/controller/*.java` (6 файлов)
- ✅ `src/main/java/com/ganttpro/config/*.java` (2 файла)

### Тесты
- ✅ `src/test/java/com/ganttpro/GanttproApplicationTests.java`
- ✅ `src/test/java/com/ganttpro/service/AuthServiceTests.java`
- ✅ `src/test/java/com/ganttpro/service/ProjectServiceTests.java`
- ✅ `src/test/java/com/ganttpro/service/TaskServiceTests.java`

### Ресурсы
- ✅ `src/main/resources/application.properties`
- ✅ `src/main/resources/templates/*.html` (7 файлов)
- ✅ `src/main/resources/static/css/style.css`
- ✅ `src/main/resources/static/js/gantt.js`

### Конфигурация
- ✅ `pom.xml`

## Команды для диагностики

### Посмотреть зависимости проекта:
```bash
mvn dependency:tree
```

### Посмотреть информацию о проекте:
```bash
mvn help:active-profiles
```

### Проверить версии плагинов:
```bash
mvn -version
```

### Выполнить конкретный тест:
```bash
mvn test -Dtest=ProjectServiceTests
```

### Запустить с включенным отладочным режимом:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.ganttpro=DEBUG"
```

## Что проверяется при сборке

### Этап Compile
- ✅ Синтаксис Java кода
- ✅ Типизация
- ✅ Импорты
- ✅ Зависимости доступны

### Этап Test
- ✅ Загрузка контекста Spring
- ✅ Регистрация пользователей
- ✅ Работа с проектами
- ✅ Работа с задачами
- ✅ Валидация данных

### Этап Package
- ✅ Создание JAR файла
- ✅ Включение всех ресурсов
- ✅ Готовность к запуску

## Возможные ошибки сборки и их решение

### Ошибка: "java version not supported"

**Решение:** Убедитесь что Java 17:
```bash
java -version
```

Должно быть 17.0.x или выше. Если нет, установите Java 17.

### Ошибка: "Cannot find symbol"

**Решение:** Очистите и переустановите:
```bash
mvn clean install
```

### Ошибка: "Port 8080 already in use"

**Решение:** Используйте другой порт:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Ошибка: "Connection refused" при тестировании БД

**Решение:** Это нормально для H2. Тесты должны создавать свои БД.

### Ошибка: Maven не найден

**Решение:** Установите Maven или используйте Maven Wrapper:
```bash
./mvnw clean install  # Linux/Mac
mvnw.cmd clean install  # Windows
```

## Финальная проверка

После успешной сборки и запуска приложения проверьте:

### В консоли:
```
2026-06-22T10:30:48.456+00:00  INFO ... Started GanttproApplication in X.XXX seconds
```

### В браузере (http://localhost:8080):
- ✅ Видна страница входа
- ✅ Логотип "📊 GanttPRO" видна
- ✅ Форма входа содержит поля email и пароль

### После входа (demo@example.com / demo123):
- ✅ Видна страница Dashboard
- ✅ Видны проекты в списке
- ✅ Видна диаграмма Ганта в проектах
- ✅ Видны задачи в таблице

## Успех!

Если все прошло успешно, проект готов к работе! 🎉

## Помощь и поддержка

Если что-то не работает:

1. Проверьте требования (Java 17+, Maven 3.6+)
2. Посмотрите полные логи в консоли
3. Читайте SETUP.md и README.md
4. Проверьте файл TESTING.md для более подробного тестирования

---

**Статус проекта:** ✅ Полностью готов к использованию
