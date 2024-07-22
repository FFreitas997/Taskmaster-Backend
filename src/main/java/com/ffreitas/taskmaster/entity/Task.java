package com.ffreitas.taskmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task")
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskComment> comments;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskAttachment> attachments;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at", insertable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "last_modified_by", insertable = false)
    @LastModifiedBy
    private String lastModifiedBy;
}
