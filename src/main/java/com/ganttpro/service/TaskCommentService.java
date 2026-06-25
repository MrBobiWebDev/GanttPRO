package com.ganttpro.service;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskComment;
import com.ganttpro.model.User;
import com.ganttpro.repository.TaskCommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskCommentService {
    private final TaskCommentRepository commentRepository;

    public TaskCommentService(TaskCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public TaskComment addComment(Task task, User author, String text) {
        TaskComment comment = new TaskComment(task, author, text);
        return commentRepository.save(comment);
    }

    public TaskComment updateComment(TaskComment comment, User user, String text) throws Exception {
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new Exception("Только автор может редактировать комментарий");
        }
        comment.setText(text);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(TaskComment comment, User user, Project project) throws Exception {
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new Exception("Только автор может удалить комментарий");
        }
        commentRepository.delete(comment);
    }

    public List<TaskComment> getComments(Task task) {
        return commentRepository.findByTaskOrderByCreatedAtAsc(task);
    }

    public Optional<TaskComment> getComment(Long id, Task task) {
        return commentRepository.findByIdAndTask(id, task);
    }
}
