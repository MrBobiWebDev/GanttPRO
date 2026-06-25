package com.ganttpro.model;

public enum NotificationType {
    TASK_ASSIGNED("Задача назначена"),
    TASK_UPDATED("Задача обновлена"),
    TASK_OVERDUE("Задача просрочена"),
    COMMENT_ADDED("Добавлен комментарий"),
    ROLE_CHANGED("Роль изменена"),
    DEPENDENCY_CONFLICT("Конфликт зависимости");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
