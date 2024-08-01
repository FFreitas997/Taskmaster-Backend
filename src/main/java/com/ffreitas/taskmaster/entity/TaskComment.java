package com.ffreitas.taskmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task_comments")
@EntityListeners(AuditingEntityListener.class)
public class TaskComment {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "comment", nullable = false, length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

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
