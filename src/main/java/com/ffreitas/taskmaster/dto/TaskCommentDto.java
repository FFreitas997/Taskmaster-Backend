package com.ffreitas.taskmaster.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.ffreitas.taskmaster.entity.TaskComment}
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TaskCommentDto(
        Integer id,
        String comment,
        LocalDateTime lastModifiedAt
) implements Serializable {
}