package com.ganttpro.model;

public enum TaskStatus {
    TODO("Запланирована"),
    IN_PROGRESS("В работе"),
    DONE("Выполнена"),
    BLOCKED("Заблокирована");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
