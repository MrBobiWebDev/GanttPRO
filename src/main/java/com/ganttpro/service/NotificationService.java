package com.ganttpro.service;

import com.ganttpro.model.Notification;
import com.ganttpro.model.NotificationType;
import com.ganttpro.model.User;
import com.ganttpro.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User user, String title, String message, NotificationType type) {
        return createNotification(user, title, message, type, null, null);
    }

    public Notification createNotification(User user, String title, String message, NotificationType type,
                                          Long projectId, Long taskId) {
        Notification notification = new Notification(user, title, message, type);
        notification.setRelatedProjectId(projectId);
        notification.setRelatedTaskId(taskId);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markRead(Long id, User user) throws Exception {
        Optional<Notification> notif = notificationRepository.findById(id);
        if (notif.isEmpty()) {
            throw new Exception("Уведомление не найдено");
        }
        if (!notif.get().getUser().getId().equals(user.getId())) {
            throw new Exception("Это уведомление не для вас");
        }
        notif.get().setRead(true);
        notificationRepository.save(notif.get());
    }

    public void markAllRead(User user) {
        List<Notification> unread = getUnreadNotifications(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
