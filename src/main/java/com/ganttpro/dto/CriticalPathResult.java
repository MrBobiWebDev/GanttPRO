package com.ganttpro.dto;

import java.util.List;

public class CriticalPathResult {
    private List<Long> taskIds;
    private Long totalDurationDays;
    private Boolean hasCycle;
    private String message;

    public CriticalPathResult(List<Long> taskIds, Long totalDurationDays, Boolean hasCycle, String message) {
        this.taskIds = taskIds;
        this.totalDurationDays = totalDurationDays;
        this.hasCycle = hasCycle;
        this.message = message;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Long getTotalDurationDays() {
        return totalDurationDays;
    }

    public void setTotalDurationDays(Long totalDurationDays) {
        this.totalDurationDays = totalDurationDays;
    }

    public Boolean getHasCycle() {
        return hasCycle;
    }

    public void setHasCycle(Boolean hasCycle) {
        this.hasCycle = hasCycle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
