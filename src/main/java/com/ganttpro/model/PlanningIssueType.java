package com.ganttpro.model;

public enum PlanningIssueType {
    DEPENDENCY_DATE_CONFLICT("Конфликт дат зависимости"),
    TASK_OUTSIDE_PROJECT_DATES("Задача вне дат проекта"),
    OVERDUE_TASK("Просроченная задача"),
    TASK_WITHOUT_ASSIGNEE("Задача без исполнителя"),
    ZERO_DURATION_TASK("Задача без длительности"),
    BLOCKED_WITHOUT_REASON("Заблокированная задача без причины");

    private final String displayName;

    PlanningIssueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
