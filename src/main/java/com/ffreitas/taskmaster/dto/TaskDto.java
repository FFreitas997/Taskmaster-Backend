package com.ffreitas.taskmaster.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ffreitas.taskmaster.entity.TaskComment;
import com.ffreitas.taskmaster.entity.TaskPriority;
import com.ffreitas.taskmaster.entity.TaskStatus;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.ffreitas.taskmaster.entity.Task}
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TaskDto(
        Integer id,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskStatus status,
        TaskPriority priority,
        List<TaskCommentDto> comments,
        LocalDateTime lastModifiedAt
) implements Serializable {
}