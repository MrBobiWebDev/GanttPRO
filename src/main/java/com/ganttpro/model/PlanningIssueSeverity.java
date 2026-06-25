package com.ganttpro.model;

public enum PlanningIssueSeverity {
    INFO("Информация"),
    WARNING("Предупреждение"),
    CRITICAL("Критично");

    private final String displayName;

    PlanningIssueSeverity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
