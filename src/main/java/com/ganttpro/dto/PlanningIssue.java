package com.ganttpro.dto;

import com.ganttpro.model.PlanningIssueType;
import com.ganttpro.model.PlanningIssueSeverity;

public class PlanningIssue {
    private PlanningIssueType type;
    private Long taskId;
    private String taskTitle;
    private PlanningIssueSeverity severity;
    private String message;

    public PlanningIssue(PlanningIssueType type, Long taskId, String taskTitle,
                         PlanningIssueSeverity severity, String message) {
        this.type = type;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.severity = severity;
        this.message = message;
    }

    public PlanningIssueType getType() {
        return type;
    }

    public void setType(PlanningIssueType type) {
        this.type = type;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public PlanningIssueSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(PlanningIssueSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
