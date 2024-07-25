package com.ffreitas.taskmaster.dto;

import com.ffreitas.taskmaster.entity.TaskPriority;
import com.ffreitas.taskmaster.entity.TaskStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.ffreitas.taskmaster.entity.Task}
 */
public record RequestTaskDto(

        @NotNull(message = "Title is required")
        @Length(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
        String title,

        @Length(max = 500, message = "Description must be less than 500 characters")
        String description,

        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,

        @NotNull(message = "Status is required")
        TaskStatus status,

        @NotNull(message = "Priority is required")
        TaskPriority priority
) implements Serializable { }