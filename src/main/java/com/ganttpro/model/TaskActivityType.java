package com.ganttpro.model;

public enum TaskActivityType {
    CREATED("Создана"),
    UPDATED("Обновлена"),
    STATUS_CHANGED("Статус изменён"),
    PROGRESS_CHANGED("Прогресс изменён"),
    ASSIGNEE_CHANGED("Исполнитель изменён"),
    PRIORITY_CHANGED("Приоритет изменён"),
    COMMENT_ADDED("Добавлен комментарий"),
    COMMENT_UPDATED("Комментарий обновлён"),
    COMMENT_DELETED("Комментарий удалён"),
    DEPENDENCY_ADDED("Зависимость добавлена"),
    DEPENDENCY_REMOVED("Зависимость удалена");

    private final String displayName;

    TaskActivityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
