package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Integer> {
    @Query("select t from TaskComment t where t.task.id = ?1")
    List<TaskComment> findByTask_Id(@NonNull Integer id);
}