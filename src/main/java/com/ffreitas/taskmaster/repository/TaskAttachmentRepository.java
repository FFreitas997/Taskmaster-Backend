package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Integer> {
}