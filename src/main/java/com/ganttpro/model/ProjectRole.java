package com.ganttpro.model;

public enum ProjectRole {
    OWNER("Владелец"),
    EDITOR("Редактор"),
    VIEWER("Просмотр");

    private final String displayName;

    ProjectRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
