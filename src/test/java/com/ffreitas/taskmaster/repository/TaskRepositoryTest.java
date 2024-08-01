package com.ffreitas.taskmaster.repository;

import com.ffreitas.taskmaster.TestcontainersConfiguration;
import com.ffreitas.taskmaster.entity.Task;
import com.ffreitas.taskmaster.entity.TaskPriority;
import com.ffreitas.taskmaster.entity.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DisplayName("Task Repository Test")
@SpringBootTest
class TaskRepositoryTest {

    @Autowired
    private PostgreSQLContainer<?> container;

    @Autowired
    private TaskRepository repository;

    @Test
    @DisplayName("Database container is running")
    void databaseContainerHealth() {
        assertTrue(container.isCreated());
        assertTrue(container.isRunning());
    }

    @BeforeEach
    void setUp() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Task 1 description")
                .dueDate(LocalDateTime.now().plusHours(1))
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .build();
        repository.saveAll(List.of(task));
    }

    @AfterEach
    void tearDown() { repository.deleteAll();}

    @Test
    @WithMockUser(username = "John Doe")
    void findByCreatedBy() {
        var page = repository.findByCreatedBy("John Doe", PageRequest.of(0, 10));

        assertTrue(page.hasContent());
        assertTrue(page.getTotalElements() > 0);
        assertFalse(page.getContent().isEmpty());

        var content = page.getContent().getFirst();

        assertNotNull(content);
        assertEquals("Task 1", content.getTitle());
        assertEquals("John Doe", content.getCreatedBy());
        assertNotNull(content.getCreatedAt());
    }

    @Test
    @WithMockUser(username = "John Doe")
    @DisplayName("Find by status and created by")
    void findByStatusAndCreatedBy() {
        var page = repository.findByStatusAndCreatedBy(TaskStatus.TODO, "John Doe", PageRequest.of(0, 10));

        assertTrue(page.hasContent());
        assertTrue(page.getTotalElements() > 0);
        assertFalse(page.getContent().isEmpty());

        var content = page.getContent().getFirst();

        assertNotNull(content);
        assertEquals("Task 1", content.getTitle());
        assertEquals("John Doe", content.getCreatedBy());
        assertNotNull(content.getCreatedAt());
    }

    @Test
    @WithMockUser(username = "John Doe")
    @DisplayName("Find by not status and created by")
    void findByCreatedByAndStatusNot() {
        var page = repository.findByCreatedByAndStatusNot("John Doe", TaskStatus.TODO, PageRequest.of(0, 10));

        assertFalse(page.hasContent());
        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }
}