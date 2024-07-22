package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Integer> {
}