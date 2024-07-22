package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.entity.Task;
import com.ffreitas.taskmaster.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("select t from Task t where t.createdBy = ?1")
    Page<Task> findByCreatedBy(@NonNull String createdBy, Pageable pageable);

    @Query("select t from Task t where t.status = ?1 and t.createdBy = ?2")
    Page<Task> findByStatusAndCreatedBy(@NonNull TaskStatus status, @NonNull String createdBy, Pageable pageable);
}