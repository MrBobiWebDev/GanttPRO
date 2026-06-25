package com.ganttpro.model;

public enum DependencyType {
    FINISH_TO_START("Завершение в старт");

    private final String displayName;

    DependencyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
