// Gantt Chart Utilities
// PROTECTION AGAINST REGRESSION:
// - Header дат и task bars должны использовать один и тот же ganttTimelineWidth
// - left и width рассчитываются от одного ganttTimelineStartDate
// - Отдельные scroll контейнеры для header и task bars ЗАПРЕЩЕНЫ
// - Все полоски рассчитываются относительно ganttTimelineStartDate
// - Высота строк должна быть одинакова: 60px (см. .gantt-task-row { height: 60px; })

class GanttChart {
    constructor(options = {}) {
        this.dayWidth = options.dayWidth || 32;
        this.tasks = [];
        this.minDate = null;
        this.maxDate = null;
    }

    setTasks(tasks) {
        this.tasks = tasks;
        this.calculateDateRange();
    }

    calculateDateRange() {
        if (this.tasks.length === 0) return;

        const dates = [];
        this.tasks.forEach(task => {
            if (task.startDate) dates.push(new Date(task.startDate));
            if (task.endDate) dates.push(new Date(task.endDate));
        });

        this.minDate = new Date(Math.min(...dates));
        this.maxDate = new Date(Math.max(...dates));
    }

    calculateTaskPosition(startDate) {
        if (!this.minDate) return 0;

        const taskStart = new Date(startDate);
        const diffTime = Math.abs(taskStart - this.minDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        return diffDays * this.dayWidth;
    }

    calculateTaskWidth(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

        return Math.max(diffDays * this.dayWidth, this.dayWidth);
    }

    getTimelineRange() {
        if (!this.minDate || !this.maxDate) return { start: new Date(), end: new Date() };

        return {
            start: this.minDate,
            end: this.maxDate
        };
    }

    formatDate(date) {
        return date.toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }
}

// Export for use in templates
window.GanttChart = GanttChart;
