package com.ffreitas.taskmaster.service;

import com.ffreitas.taskmaster.dto.RequestTaskCommentDto;
import com.ffreitas.taskmaster.dto.RequestTaskDto;
import com.ffreitas.taskmaster.dto.TaskCommentDto;
import com.ffreitas.taskmaster.dto.TaskDto;
import com.ffreitas.taskmaster.entity.Task;
import com.ffreitas.taskmaster.entity.TaskComment;
import com.ffreitas.taskmaster.entity.TaskStatus;
import com.ffreitas.taskmaster.repository.TaskCommentRepository;
import com.ffreitas.taskmaster.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskCommentRepository commentRepository;

    @Override
    public Page<TaskDto> getAllTasks(int page, int size) {
        log.info("Getting all tasks with pagination: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        return repository.findAll(pageable)
                .map(param -> TaskDto.builder()
                        .id(param.getId())
                        .title(param.getTitle())
                        .description(param.getDescription())
                        .dueDate(param.getDueDate())
                        .status(param.getStatus())
                        .priority(param.getPriority())
                        .lastModifiedAt(param.getLastModifiedAt())
                        .build()
                );
    }

    @Override
    public Page<TaskDto> getTasks(String name, int page, int size) {
        log.info("Getting user {} tasks with pagination: page={}, size={}", name, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        return repository.findByCreatedByAndStatusNot(name, TaskStatus.ARCHIVED, pageable)
                .map(param -> TaskDto.builder()
                        .id(param.getId())
                        .title(param.getTitle())
                        .description(param.getDescription())
                        .dueDate(param.getDueDate())
                        .status(param.getStatus())
                        .priority(param.getPriority())
                        .lastModifiedAt(param.getLastModifiedAt())
                        .build()
                );
    }

    @Override
    public Page<TaskDto> getArchivedTasks(String name, int page, int size) {
        log.info("Getting user {} archived tasks with pagination: page={}, size={}", name, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        return repository.findByStatusAndCreatedBy(TaskStatus.ARCHIVED, name, pageable)
                .map(param -> TaskDto.builder()
                        .id(param.getId())
                        .title(param.getTitle())
                        .description(param.getDescription())
                        .dueDate(param.getDueDate())
                        .status(param.getStatus())
                        .priority(param.getPriority())
                        .lastModifiedAt(param.getLastModifiedAt())
                        .build()
                );
    }

    @Override
    public TaskDto getTaskById(Integer id) {
        log.info("Getting task by ID {}", id);

        var task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID " + id));

        Function<Task, List<TaskCommentDto>> setComments = (param) -> {
            if (param.getComments() == null)
                return List.of();
            return param.getComments()
                    .stream()
                    .map(comment -> TaskCommentDto.builder()
                            .id(comment.getId())
                            .comment(comment.getComment())
                            .lastModifiedAt(comment.getLastModifiedAt())
                            .build()
                    )
                    .toList();
        };

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .priority(task.getPriority())
                .lastModifiedAt(task.getLastModifiedAt())
                .comments(setComments.apply(task))
                .build();
    }

    @Override
    public void createTask(RequestTaskDto taskDto) {
        log.info("Creating task with title: {}", taskDto.title());

        Task task = Task.builder()
                .title(taskDto.title())
                .description(taskDto.description())
                .dueDate(taskDto.dueDate())
                .status(TaskStatus.TODO)
                .priority(taskDto.priority())
                .build();

        repository.save(task);
    }

    @Override
    public void updateTask(Integer id, RequestTaskDto taskDto) {
        log.info("Updating task with ID {}", id);

        var task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID " + id));

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setDueDate(taskDto.dueDate());
        task.setPriority(taskDto.priority());
        task.setStatus(taskDto.status());

        repository.save(task);
    }

    @Override
    public void archiveTask(Integer id) {
        log.info("Archiving task with ID {}", id);

        var task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID " + id));

        task.setStatus(TaskStatus.ARCHIVED);

        repository.save(task);
    }

    @Override
    public void createTaskComment(Integer taskID, RequestTaskCommentDto request) {
        log.info("Creating comment for task with ID {}", taskID);

        var task = repository.findById(taskID)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID " + taskID));

        TaskComment comment = TaskComment.builder()
                .comment(request.comment())
                .task(task)
                .build();

        commentRepository.save(comment);
    }
}
