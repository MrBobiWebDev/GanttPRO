package com.ganttpro.repository;

import com.ganttpro.model.Task;
import com.ganttpro.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskOrderByCreatedAtAsc(Task task);
    Optional<TaskComment> findByIdAndTask(Long id, Task task);
}
