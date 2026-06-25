package com.ganttpro;

import com.ganttpro.model.*;
import com.ganttpro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class GanttproApplication {

    public static void main(String[] args) {
        SpringApplication.run(GanttproApplication.class, args);
    }

    @Bean
    @Profile("default")
    public CommandLineRunner loadDemoData(UserRepository userRepository,
                                         ProjectRepository projectRepository,
                                         TaskRepository taskRepository,
                                         ProjectMemberRepository projectMemberRepository,
                                         TaskDependencyRepository taskDependencyRepository,
                                         TaskCommentRepository taskCommentRepository,
                                         TaskActivityRepository taskActivityRepository,
                                         NotificationRepository notificationRepository,
                                         ProjectTemplateRepository projectTemplateRepository,
                                         ProjectTemplateTaskRepository projectTemplateTaskRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("demo@example.com").isEmpty()) {
                User demoUser = new User("Демо пользователь", "demo@example.com",
                        passwordEncoder.encode("demo123"));
                userRepository.save(demoUser);

                User editorUser = new User("Редактор", "editor@example.com",
                        passwordEncoder.encode("editor123"));
                userRepository.save(editorUser);

                User viewerUser = new User("Зритель", "viewer@example.com",
                        passwordEncoder.encode("viewer123"));
                userRepository.save(viewerUser);

                // Используем фиксированную дату, чтобы данные оставались статичными
                LocalDate today = LocalDate.of(2026, 6, 20);

                Project project1 = new Project(
                        "Веб-приложение GanttPRO",
                        "Разработка простого приложения для управления проектами",
                        today,
                        today.plusDays(45),
                        demoUser
                );
                projectRepository.save(project1);

                Task task1 = new Task(project1, "Дизайн интерфейса",
                        "Создать макеты страниц", today, today.plusDays(7), "Алексей");
                task1.setStatus(TaskStatus.DONE);
                task1.setProgress(100);
                task1.setPriority(TaskPriority.HIGH);
                taskRepository.save(task1);

                Task task2 = new Task(project1, "Создание моделей данных",
                        "Реализовать модели User, Project, Task", today.plusDays(5),
                        today.plusDays(12), "Мария");
                task2.setStatus(TaskStatus.IN_PROGRESS);
                task2.setProgress(75);
                task2.setPriority(TaskPriority.CRITICAL);
                taskRepository.save(task2);

                Task task3 = new Task(project1, "Авторизация и аутентификация",
                        "Реализовать регистрацию и вход", today.plusDays(8),
                        today.plusDays(15), "Иван");
                task3.setStatus(TaskStatus.IN_PROGRESS);
                task3.setProgress(50);
                task3.setPriority(TaskPriority.HIGH);
                taskRepository.save(task3);

                Task task4 = new Task(project1, "CRUD для проектов",
                        "Создать, редактировать, удалять проекты", today.plusDays(10),
                        today.plusDays(20), "Ольга");
                task4.setStatus(TaskStatus.TODO);
                task4.setProgress(0);
                task4.setPriority(TaskPriority.MEDIUM);
                taskRepository.save(task4);

                Task task5 = new Task(project1, "CRUD для задач",
                        "Создать, редактировать, удалять задачи", today.plusDays(15),
                        today.plusDays(25), "Петр");
                task5.setStatus(TaskStatus.TODO);
                task5.setProgress(0);
                task5.setPriority(TaskPriority.MEDIUM);
                taskRepository.save(task5);

                Task task6 = new Task(project1, "Диаграмма Ганта",
                        "Реализовать визуализацию диаграммы Ганта", today.plusDays(20),
                        today.plusDays(35), "Сергей");
                task6.setStatus(TaskStatus.TODO);
                task6.setProgress(0);
                task6.setPriority(TaskPriority.HIGH);
                taskRepository.save(task6);

                Task task7 = new Task(project1, "Дизайн и стили",
                        "Создать красивый интерфейс. Заблокирована ожиданием утверждения дизайна у клиента",
                        today.plusDays(30),
                        today.plusDays(40), "Елена");
                task7.setStatus(TaskStatus.BLOCKED);
                task7.setProgress(10);
                task7.setPriority(TaskPriority.LOW);
                taskRepository.save(task7);

                Task task11 = new Task(project1, "Задача без исполнителя",
                        "Эта задача еще не назначена", today.plusDays(25),
                        today.plusDays(28), null);
                task11.setStatus(TaskStatus.TODO);
                task11.setProgress(0);
                task11.setPriority(TaskPriority.MEDIUM);
                taskRepository.save(task11);

                // Просроченная задача для демонстрации
                Task task10 = new Task(project1, "Срочное исправление",
                        "Исправить критическую ошибку", today.minusDays(5),
                        today.minusDays(2), "Алексей");
                task10.setStatus(TaskStatus.TODO);
                task10.setProgress(0);
                task10.setPriority(TaskPriority.CRITICAL);
                taskRepository.save(task10);

                Project project2 = new Project(
                        "Мобильное приложение",
                        "Разработка мобильного клиента",
                        today.plusDays(14),
                        today.plusDays(60),
                        demoUser
                );
                projectRepository.save(project2);

                Task task8 = new Task(project2, "API интеграция",
                        "Подключить мобильное приложение к API", today.plusDays(14),
                        today.plusDays(21), "Дмитрий");
                task8.setStatus(TaskStatus.TODO);
                task8.setProgress(0);
                task8.setPriority(TaskPriority.CRITICAL);
                taskRepository.save(task8);

                Task task9 = new Task(project2, "Тестирование",
                        "Провести полное тестирование", today.plusDays(40),
                        today.plusDays(55), "Анна");
                task9.setStatus(TaskStatus.TODO);
                task9.setProgress(0);
                task9.setPriority(TaskPriority.HIGH);
                taskRepository.save(task9);

                ProjectMember editorMember = new ProjectMember(project1, editorUser, ProjectRole.EDITOR);
                projectMemberRepository.save(editorMember);

                ProjectMember viewerMember = new ProjectMember(project1, viewerUser, ProjectRole.VIEWER);
                projectMemberRepository.save(viewerMember);

                ProjectMember member2 = new ProjectMember(project2, editorUser, ProjectRole.VIEWER);
                projectMemberRepository.save(member2);

                TaskDependency dep1 = new TaskDependency(project1, task2, task1, DependencyType.FINISH_TO_START);
                taskDependencyRepository.save(dep1);

                TaskDependency dep2 = new TaskDependency(project1, task3, task1, DependencyType.FINISH_TO_START);
                taskDependencyRepository.save(dep2);

                TaskDependency dep3 = new TaskDependency(project1, task4, task2, DependencyType.FINISH_TO_START);
                taskDependencyRepository.save(dep3);

                // Добавить комментарии и активность
                TaskComment comment1 = new TaskComment(task1, demoUser, "Хороший дизайн! Готово к разработке.");
                taskCommentRepository.save(comment1);

                TaskComment comment2 = new TaskComment(task2, editorUser, "Отличная работа! Свяжитесь со мной если нужна помощь.");
                taskCommentRepository.save(comment2);

                TaskActivity activity1 = new TaskActivity(task1, demoUser, TaskActivityType.CREATED, "Задача создана");
                taskActivityRepository.save(activity1);

                TaskActivity activity2 = new TaskActivity(task1, demoUser, TaskActivityType.STATUS_CHANGED, "Статус изменён на DONE");
                taskActivityRepository.save(activity2);

                // Добавить уведомления
                Notification notif1 = new Notification(demoUser, "Задача назначена", "Вам назначена новая задача: Дизайн интерфейса", NotificationType.TASK_ASSIGNED);
                notif1.setRelatedProjectId(project1.getId());
                notif1.setRelatedTaskId(task1.getId());
                notificationRepository.save(notif1);

                Notification notif2 = new Notification(demoUser, "Комментарий добавлен", "Редактор добавил комментарий к задаче: Создание моделей данных", NotificationType.COMMENT_ADDED);
                notif2.setRelatedProjectId(project1.getId());
                notif2.setRelatedTaskId(task2.getId());
                notificationRepository.save(notif2);

                Notification notif3 = new Notification(demoUser, "Просроченная задача", "Задача 'Срочное исправление' просрочена", NotificationType.TASK_OVERDUE);
                notif3.setRelatedProjectId(project1.getId());
                notif3.setRelatedTaskId(task10.getId());
                notificationRepository.save(notif3);

                // Создать шаблоны проектов
                if (projectTemplateRepository.findByName("Разработка веб-приложения").isEmpty()) {
                    ProjectTemplate template1 = new ProjectTemplate(
                            "Разработка веб-приложения",
                            "Типовой шаблон для разработки веб-приложения с этапами от анализа до релиза",
                            "Разработка"
                    );
                    projectTemplateRepository.save(template1);

                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Анализ требований", "Сбор и анализ требований", 0, 5,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 0
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Проектирование архитектуры", "Создание архитектуры приложения", 5, 7,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 1
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Разработка backend", "Реализация бэкенда", 12, 14,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 2
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Разработка frontend", "Реализация фронтенда", 12, 14,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 3
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Тестирование", "Полное тестирование приложения", 26, 7,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 4
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template1, "Релиз", "Подготовка и развертывание релиза", 33, 3,
                            TaskStatus.TODO, TaskPriority.CRITICAL, null, 5
                    ));

                    ProjectTemplate template2 = new ProjectTemplate(
                            "Учебный проект",
                            "Шаблон для типового учебного проекта",
                            "Образование"
                    );
                    projectTemplateRepository.save(template2);

                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Выбор темы", "Выбрать тему проекта", 0, 3,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 0
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Сбор материалов", "Собрать необходимые материалы", 3, 5,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 1
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Написание теоретической части", "Написать теоретическую часть", 8, 10,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 2
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Разработка практической части", "Выполнить практическую часть", 18, 7,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 3
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Подготовка презентации", "Создать презентацию", 25, 3,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 4
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template2, "Защита", "Защита проекта", 28, 1,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 5
                    ));

                    ProjectTemplate template3 = new ProjectTemplate(
                            "Маркетинговая кампания",
                            "Шаблон для планирования маркетинговой кампании",
                            "Маркетинг"
                    );
                    projectTemplateRepository.save(template3);

                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template3, "Анализ аудитории", "Провести анализ целевой аудитории", 0, 4,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 0
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template3, "Подготовка креативов", "Создать креативные материалы", 4, 6,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 1
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template3, "Настройка каналов", "Настроить каналы распределения", 10, 3,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 2
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template3, "Запуск кампании", "Запустить маркетинговую кампанию", 13, 14,
                            TaskStatus.TODO, TaskPriority.HIGH, null, 3
                    ));
                    projectTemplateTaskRepository.save(new ProjectTemplateTask(
                            template3, "Анализ результатов", "Провести анализ результатов кампании", 27, 3,
                            TaskStatus.TODO, TaskPriority.MEDIUM, null, 4
                    ));
                }
            }
        };
    }
}
