package com.ganttpro.dto;

public class AssigneeWorkload {
    private String assigneeName;
    private Long totalTasks;
    private Long inProgressTasks;
    private Long doneTasks;
    private Long overdueTasks;
    private Double averageProgress;
    private Long criticalPriorityTasks;
    private Long criticalPathTasks;

    public AssigneeWorkload(String assigneeName) {
        this.assigneeName = assigneeName;
        this.totalTasks = 0L;
        this.inProgressTasks = 0L;
        this.doneTasks = 0L;
        this.overdueTasks = 0L;
        this.averageProgress = 0.0;
        this.criticalPriorityTasks = 0L;
        this.criticalPathTasks = 0L;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public Long getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(Long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public Long getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(Long doneTasks) {
        this.doneTasks = doneTasks;
    }

    public Long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(Long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public Double getAverageProgress() {
        return averageProgress;
    }

    public void setAverageProgress(Double averageProgress) {
        this.averageProgress = averageProgress;
    }

    public Long getCriticalPriorityTasks() {
        return criticalPriorityTasks;
    }

    public void setCriticalPriorityTasks(Long criticalPriorityTasks) {
        this.criticalPriorityTasks = criticalPriorityTasks;
    }

    public Long getCriticalPathTasks() {
        return criticalPathTasks;
    }

    public void setCriticalPathTasks(Long criticalPathTasks) {
        this.criticalPathTasks = criticalPathTasks;
    }
}
