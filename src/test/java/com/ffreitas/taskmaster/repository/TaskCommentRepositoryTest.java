package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.TestcontainersConfiguration;
import com.ffreitas.taskmaster.entity.Task;
import com.ffreitas.taskmaster.entity.TaskComment;
import com.ffreitas.taskmaster.entity.TaskPriority;
import com.ffreitas.taskmaster.entity.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DisplayName("Task Comment Repository Test")
@SpringBootTest
class TaskCommentRepositoryTest {

    @Autowired
    private PostgreSQLContainer<?> container;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    private List<Task> savedTasks = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Task 1 description")
                .dueDate(LocalDateTime.now().plusHours(1))
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .build();
        savedTasks = repository.saveAll(List.of(task));

        if (savedTasks.isEmpty())
            return;

        TaskComment taskComment = TaskComment.builder()
                .task(savedTasks.getFirst())
                .comment("Task 1 comment")
                .build();

        taskCommentRepository.save(taskComment);
    }

    @AfterEach
    void tearDown() { repository.deleteAll(); }

    @Test
    @DisplayName("Database container is running")
    void databaseContainerHealth() {
        assertTrue(container.isCreated());
        assertTrue(container.isRunning());
    }

    @Test
    @DisplayName("Find comment by task id")
    void findByTask_Id() {
        assertFalse(savedTasks.isEmpty());

        List<TaskComment> taskComments = taskCommentRepository.findByTask_Id(savedTasks.getFirst().getId());

        assertFalse(taskComments.isEmpty());
        assertEquals("Task 1 comment", taskComments.getFirst().getComment());
    }
}