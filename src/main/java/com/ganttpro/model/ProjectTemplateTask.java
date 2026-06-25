package com.ganttpro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_template_tasks")
public class ProjectTemplateTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ProjectTemplate template;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer offsetStartDays;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private String assigneeName;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    public ProjectTemplateTask() {
    }

    public ProjectTemplateTask(ProjectTemplate template, String title, String description,
                              Integer offsetStartDays, Integer durationDays, TaskStatus status,
                              TaskPriority priority, String assigneeName, Integer orderIndex) {
        this.template = template;
        this.title = title;
        this.description = description;
        this.offsetStartDays = offsetStartDays;
        this.durationDays = durationDays;
        this.status = status;
        this.priority = priority;
        this.assigneeName = assigneeName;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProjectTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ProjectTemplate template) {
        this.template = template;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOffsetStartDays() {
        return offsetStartDays;
    }

    public void setOffsetStartDays(Integer offsetStartDays) {
        this.offsetStartDays = offsetStartDays;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
