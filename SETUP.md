# 🛠️ Установка и запуск GanttPRO

Это руководство поможет вам установить и запустить приложение GanttPRO.

## Системные требования

- **Java 17** или выше
- **Maven 3.6** или выше

### Проверка требований

**Проверить версию Java:**
```bash
java -version
```

Должно вывести что-то вроде:
```
openjdk version "17.0.1" 2021-10-19
OpenJDK Runtime Environment (build 17.0.1+...)
```

**Проверить версию Maven:**
```bash
mvn -version
```

Должно вывести что-то вроде:
```
Apache Maven 3.8.1 (...)
```

## Скачивание Java и Maven

### Если Java не установлена:

1. Скачайте [OpenJDK 17](https://jdk.java.net/17/)
2. Установите в удобное место
3. Добавьте в переменную окружения `PATH`

### Если Maven не установлен:

1. Скачайте [Apache Maven](https://maven.apache.org/download.cgi)
2. Распакуйте в удобное место
3. Добавьте в переменную окружения `PATH`

Или установите через пакетный менеджер:
- **Windows (Chocolatey):** `choco install maven`
- **Mac (Homebrew):** `brew install maven`
- **Ubuntu/Debian:** `sudo apt install maven`

## Установка проекта

### Шаг 1: Перейти в директорию проекта

**Windows:**
```bash
cd "C:\Users\diana\OneDrive\Рабочий стол\GanttPRO"
```

**Linux/Mac:**
```bash
cd /path/to/GanttPRO
```

### Шаг 2: Установить зависимости

**Вариант 1 - Использовать Maven Wrapper (рекомендуется):**

Windows:
```bash
mvnw.cmd clean install
```

Linux/Mac:
```bash
./mvnw clean install
```

**Вариант 2 - Использовать системный Maven:**

```bash
mvn clean install
```

Этот процесс скачает все зависимости и скомпилирует проект. Это может занять несколько минут.

## Запуск тестов

### Быстрый способ (используя скрипты):

**Windows:**
```bash
run-tests.cmd
```

**Linux/Mac:**
```bash
./run-tests.sh
```

### Использование Maven напрямую:

**Windows:**
```bash
mvnw.cmd clean test
```

**Linux/Mac:**
```bash
./mvnw clean test
```

**Или с обычным Maven:**
```bash
mvn clean test
```

После успешного прохождения всех тестов вы должны увидеть:
```
[INFO] BUILD SUCCESS
```

## Запуск приложения

### Быстрый способ (используя скрипты):

**Windows:**
```bash
run-app.cmd
```

**Linux/Mac:**
```bash
./run-app.sh
```

### Использование Maven напрямую:

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Или с обычным Maven:**
```bash
mvn spring-boot:run
```

После успешного запуска вы должны увидеть в консоли:
```
[INFO] Started GanttproApplication in X.XXX seconds (JVM running for X.XXX)
```

### Открыть приложение в браузере

Откройте URL в браузере:
```
http://localhost:8080
```

Вас должны перенаправить на страницу входа.

## Первый вход

Используйте демо-учетные данные:

- **Email:** demo@example.com
- **Пароль:** demo123

После входа вы попадете на Dashboard с двумя демо-проектами.

## Остановка приложения

Нажмите **Ctrl+C** в терминале, где запущено приложение.

## Решение проблем

### Проблема: "Java is not recognized"

**Windows:**
1. Откройте "Переменные окружения"
2. Создайте переменную `JAVA_HOME` с путем к папке Java
3. Перезагрузитесь

**Linux/Mac:**
```bash
export JAVA_HOME=/path/to/java
export PATH=$JAVA_HOME/bin:$PATH
```

### Проблема: "Maven is not recognized"

**Windows:**
1. Откройте "Переменные окружения"
2. Создайте переменную `M2_HOME` с путем к папке Maven
3. Добавьте `%M2_HOME%\bin` в PATH
4. Перезагрузитесь

**Linux/Mac:**
```bash
export M2_HOME=/path/to/maven
export PATH=$M2_HOME/bin:$PATH
```

### Проблема: "Port 8080 is already in use"

Запустите на другом порту:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

Потом откройте: `http://localhost:8081`

### Проблема: "Tests fail"

1. Убедитесь, что используется Java 17+
2. Очистите кеш Maven:
   ```bash
   mvn clean
   ```
3. Установите зависимости заново:
   ```bash
   mvn install
   ```

## Команды для разработки

### Собрать проект без тестов
```bash
mvn clean package -DskipTests
```

### Запустить с логированием
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.ganttpro=DEBUG"
```

### Запустить конкретный тест
```bash
mvn test -Dtest=ProjectServiceTests
```

### Очистить артефакты
```bash
mvn clean
```

### Просмотреть зависимости
```bash
mvn dependency:tree
```

## IDE (Интегрированные среды разработки)

### IntelliJ IDEA

1. Откройте File > Open
2. Выберите папку проекта
3. IDEA автоматически распознает Maven проект
4. Нажмите Run > Run 'GanttproApplication'

### Visual Studio Code

1. Установите расширение "Extension Pack for Java"
2. Откройте папку проекта
3. Нажмите на иконку "Run" слева
4. Выберите GanttproApplication

### Eclipse

1. File > Import > Maven > Existing Maven Projects
2. Выберите папку проекта
3. Run > Run As > Maven Build
4. Введите `spring-boot:run` в Goals

## Следующие шаги

После успешного запуска приложения:

1. Изучите функциональность через интерфейс
2. Прочитайте [README.md](README.md) для полной документации
3. Прочитайте [TESTING.md](TESTING.md) для подробного тестирования
4. Прочитайте [QUICK_START.md](QUICK_START.md) для быстрого старта

## Основные файлы проекта

- `pom.xml` - Конфигурация Maven
- `src/main/java` - Java исходный код
- `src/main/resources/templates` - HTML шаблоны
- `src/main/resources/static` - CSS и JavaScript
- `src/test/java` - Тесты

## Полезные URL (после запуска)

- Приложение: http://localhost:8080
- Вход: http://localhost:8080/login
- Регистрация: http://localhost:8080/register
- H2 Console: http://localhost:8080/h2-console
- REST API: http://localhost:8080/api/

## Контакт и поддержка

Если у вас возникли проблемы:

1. Проверьте требования (Java 17+, Maven 3.6+)
2. Посмотрите логи в консоли
3. Очистите кеш Maven: `mvn clean`
4. Переустановите зависимости: `mvn install`

## Успешно! 🎉

Если вы дошли до этого момента и всё работает, поздравляем! 

Приложение GanttPRO готово к использованию. Начните с демо-данных и создавайте свои проекты и задачи!
