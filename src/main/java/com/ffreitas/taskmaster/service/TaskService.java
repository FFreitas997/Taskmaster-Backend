package com.ffreitas.taskmaster.service;

import com.ffreitas.taskmaster.dto.RequestTaskCommentDto;
import com.ffreitas.taskmaster.dto.RequestTaskDto;
import com.ffreitas.taskmaster.dto.TaskDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {
    Page<TaskDto> getAllTasks(int page, int size);

    Page<TaskDto> getTasks(String name, int page, int size);

    Page<TaskDto> getArchivedTasks(String name, int page, int size);

    TaskDto getTaskById(Integer id);

    void createTask(RequestTaskDto taskDto);

    void updateTask(Integer id, RequestTaskDto taskDto);

    void archiveTask(Integer id);

    void createTaskComment(Integer taskID, RequestTaskCommentDto request);
}
