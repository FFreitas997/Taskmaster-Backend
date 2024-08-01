package com.ffreitas.taskmaster.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * DTO for {@link com.ffreitas.taskmaster.entity.TaskComment}
 */
public record RequestTaskCommentDto(
        @NotNull(message = "Comment is required")
        @NotEmpty(message = "Comment is required")
        @Length(max = 1000, message = "Comment must be less than 1000 characters")
        String comment
) implements Serializable {
}