# Решение проблем при запуске GanttPRO

## Проблема: Консоль закрывается сразу после запуска

Это означает, что произошла ошибка при запуске приложения. Вот как её найти:

### Вариант 1: Запуск с логированием (PowerShell)

```powershell
cd "c:\Users\diana\OneDrive\Рабочий стол\GanttPRO"
.\test-compile.ps1
```

Это покажет все ошибки компиляции в окне PowerShell.

### Вариант 2: Запуск с сохранением логов (CMD)

```cmd
cd "c:\Users\diana\OneDrive\Рабочий стол\GanttPRO"
run-with-logging.cmd
```

Ошибки будут сохранены в файл `run-output.log`

### Вариант 3: Только компиляция

```cmd
cd "c:\Users\diana\OneDrive\Рабочий стол\GanttPRO"
compile-only.cmd
```

Результат в файле `compile-output.log`

## Частые ошибки

### 1. Ошибка "Cannot find symbol: class ProjectTemplateRepository"

**Решение:** Убедитесь, что файл:
```
src/main/java/com/ganttpro/repository/ProjectTemplateRepository.java
```
существует и содержит правильный код.

### 2. Ошибка "cannot find symbol: constructor ProjectTemplateTask"

**Решение:** Проверьте конструктор в классе:
```
src/main/java/com/ganttpro/model/ProjectTemplateTask.java
```

### 3. Ошибка компиляции в GanttproApplication

**Решение:** Убедитесь, что все закрывающие скобки на месте. Проверьте конец файла:
```
src/main/java/com/ganttpro/GanttproApplication.java
```

### 4. Ошибка импорта в CriticalPathService

**Решение:** Уже исправлено - было `import java.*;`, переделано на `import java.util.*;`

## Если ошибку найти не удалось

1. Закройте IDE (если используется)
2. Удалите папку `target`:
   ```cmd
   cd "c:\Users\diana\OneDrive\Рабочий стол\GanttPRO"
   rmdir /s /q target
   ```
3. Запустите:
   ```cmd
   .\test-compile.ps1
   ```

## Проверка зависимостей

Убедитесь, что установлены:
- Java 17+: `java -version`
- Maven: `mvn --version` (должен установиться автоматически через mvnw)

## Если всё не работает

Скопируйте ошибку из лога и отправьте её - я помогу исправить.

Логи находятся в:
- `run-output.log` — полный логист запуска
- `compile-output.log` — логист компиляции
