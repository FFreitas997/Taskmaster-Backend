package com.ffreitas.taskmaster.controller;

import com.ffreitas.taskmaster.dto.RequestTaskCommentDto;
import com.ffreitas.taskmaster.dto.RequestTaskDto;
import com.ffreitas.taskmaster.dto.TaskDto;
import com.ffreitas.taskmaster.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Features", description = "These operations are related to task management.")
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasRole('admin')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Get all tasks", description = "This operation retrieves all tasks.")
    @GetMapping("/all")
    public Page<TaskDto> getAllTasks(Authentication authentication, @RequestParam(required = false, name = "page", defaultValue = "0") int page, @RequestParam(required = false, name = "size", defaultValue = "10") int size) {
        log.info("Getting all tasks for admin user {}", authentication.getName());
        return taskService.getAllTasks(page, size);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Get User active tasks", description = "This operation retrieves all tasks related to the authenticated user.")
    @GetMapping
    public Page<TaskDto> getTasks(Authentication authentication, @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Getting user {} tasks", authentication.getName());
        return taskService.getTasks(authentication.getName(), page, size);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Get User archived tasks", description = "This operation retrieves all archived tasks related to the authenticated user.")
    @GetMapping("/archived")
    public Page<TaskDto> getArchivedTasks(Authentication authentication, @RequestParam(required = false, name = "page", defaultValue = "0") int page, @RequestParam(required = false, name = "size", defaultValue = "10") int size) {
        log.info("Get user {} archived tasks", authentication.getName());
        return taskService.getArchivedTasks(authentication.getName(), page, size);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Get Task by ID", description = "This operation retrieves a task by its ID.")
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Integer id) {
        log.info("Getting task by ID {}", id);
        return taskService.getTaskById(id);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Archive Task", description = "This operation archives a task by its ID.")
    @PatchMapping("/archive/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void archiveTask(@PathVariable Integer id) {
        log.info("Archiving task {}", id);
        taskService.archiveTask(id);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Create Task", description = "This operation creates a new task.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody @Valid RequestTaskDto taskDto) {
        log.info("Creating task with title: {}", taskDto.title());
        taskService.createTask(taskDto);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Update Task", description = "This operation updates a task by its ID.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateTask(@PathVariable Integer id, @RequestBody @Valid RequestTaskDto taskDto) {
        log.info("Updating task with ID: {}", id);
        taskService.updateTask(id, taskDto);
    }

    @PreAuthorize("hasRole('manager')")
    @SecurityRequirement(name = "JSON Web Token (JWT)")
    @Operation(summary = "Create Task Comment", description = "This operation creates a new comment for a task.")
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTaskComment(@PathVariable Integer id, @RequestBody @Valid RequestTaskCommentDto comment) {
        log.info("Creating comment for task with ID: {}", id);
        taskService.createTaskComment(id, comment);
    }
}
