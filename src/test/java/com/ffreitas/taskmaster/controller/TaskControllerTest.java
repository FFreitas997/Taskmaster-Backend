package com.ffreitas.taskmaster.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ffreitas.taskmaster.TestcontainersConfiguration;
import com.ffreitas.taskmaster.dto.RequestTaskCommentDto;
import com.ffreitas.taskmaster.dto.RequestTaskDto;
import com.ffreitas.taskmaster.entity.Task;
import com.ffreitas.taskmaster.entity.TaskPriority;
import com.ffreitas.taskmaster.entity.TaskStatus;
import com.ffreitas.taskmaster.repository.TaskCommentRepository;
import com.ffreitas.taskmaster.repository.TaskRepository;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DisplayName("Task Controller => Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    static final String GRANT_TYPE = "client_credentials";
    static final String CLIENT_ID = "taskmaster-client";
    static final String CLIENT_SECRET = "du8r5RiquFPcA9rWMwifWsV0cBx52S98";

    @LocalServerPort
    private Integer port;

    @Autowired
    private PostgreSQLContainer<?> container;

    @Autowired
    private KeycloakContainer keycloak;

    @Autowired
    private OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Database container is running")
    void databaseContainerHealth() {
        assertTrue(container.isCreated());
        assertTrue(container.isRunning());
    }

    @Test
    @DisplayName("Keycloak container is running")
    void keycloakContainerHealth() {
        assertTrue(keycloak.isCreated());
        assertTrue(keycloak.isRunning());
    }

    @Test
    @DisplayName("Controller method getAllTasks => test if the endpoint has a expected behavior and is secured")
    void getAllTasks() {
        List<Task> tasks = List.of(
                Task.builder()
                        .title("Task 1")
                        .description("Description 1")
                        .dueDate(LocalDateTime.now().plusDays(1))
                        .status(TaskStatus.COMPLETED)
                        .priority(TaskPriority.HIGH)
                        .build(),
                Task.builder()
                        .title("Task 2")
                        .description("Description 2")
                        .dueDate(LocalDateTime.now().plusDays(2))
                        .status(TaskStatus.FAILED)
                        .priority(TaskPriority.MEDIUM)
                        .build()
        );
        taskRepository.saveAll(tasks);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tasks/all")
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tasks/all")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(2))
                .body("content.size()", equalTo(2))
                .body("content.title", hasItems("Task 1", "Task 2"))
                .body("content.description", hasItems("Description 1", "Description 2"))
                .body("content.status", hasItems("COMPLETED", "FAILED"))
                .body("content.priority", hasItems("HIGH", "MEDIUM"))
                .body("content.dueDate", notNullValue())
                .body("content.id", notNullValue());

    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method getTasks => test if the endpoint has a expected behavior and is secured")
    void getTasks() {
        List<Task> tasks = List.of(
                Task.builder()
                        .title("Task 1")
                        .description("Description 1")
                        .dueDate(LocalDateTime.now().plusDays(1))
                        .status(TaskStatus.COMPLETED)
                        .priority(TaskPriority.HIGH)
                        .build(),
                Task.builder()
                        .title("Task 2")
                        .description("Description 2")
                        .dueDate(LocalDateTime.now().plusDays(2))
                        .status(TaskStatus.FAILED)
                        .priority(TaskPriority.MEDIUM)
                        .build(),
                Task.builder()
                        .title("Task 3")
                        .description("Description 3")
                        .dueDate(LocalDateTime.now().plusDays(2))
                        .status(TaskStatus.ARCHIVED)
                        .priority(TaskPriority.MEDIUM)
                        .build()
        );
        taskRepository.saveAll(tasks);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tasks")
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/tasks")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(2))
                .body("content.title", hasItems("Task 1", "Task 2"))
                .body("content.id", notNullValue());
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method getArchivedTasks => test if the endpoint has a expected behavior and is secured")
    void getArchivedTasks() {
        List<Task> tasks = List.of(
                Task.builder()
                        .title("Task 1")
                        .description("Description 1")
                        .dueDate(LocalDateTime.now().plusDays(1))
                        .status(TaskStatus.COMPLETED)
                        .priority(TaskPriority.HIGH)
                        .build(),
                Task.builder()
                        .title("Task 2")
                        .description("Description 2")
                        .dueDate(LocalDateTime.now().plusDays(2))
                        .status(TaskStatus.ARCHIVED)
                        .priority(TaskPriority.MEDIUM)
                        .build(),
                Task.builder()
                        .title("Task 3")
                        .description("Description 3")
                        .dueDate(LocalDateTime.now().plusDays(2))
                        .status(TaskStatus.ARCHIVED)
                        .priority(TaskPriority.MEDIUM)
                        .build()
        );

        taskRepository.saveAll(tasks);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tasks/archived")
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/tasks/archived")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(2))
                .body("content.title", hasItems("Task 2", "Task 3"))
                .body("content.id", notNullValue());
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method getTaskById => test if the endpoint has a expected behavior and is secured")
    void getTaskById() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        var savedTask = taskRepository.save(task);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/tasks/" + savedTask.getId())
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/tasks/" + savedTask.getId())
                .then()
                .statusCode(200)
                .body("title", equalTo("Task 1"))
                .body("description", equalTo("Description 1"))
                .body("status", equalTo("IN_PROGRESS"))
                .body("priority", equalTo("HIGH"))
                .body("dueDate", notNullValue())
                .body("id", notNullValue());
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method archiveTask => test if the endpoint has a expected behavior and is secured")
    void archiveTask() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.HIGH)
                .build();

        var savedTask = taskRepository.save(task);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .when()
                .patch("/api/v1/tasks/archive/" + savedTask.getId())
                .then()
                .statusCode(401);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/tasks/archive/" + savedTask.getId())
                .then()
                .statusCode(202);

        var result = taskRepository.findById(savedTask.getId());

        assertTrue(result.isPresent());
        assertEquals(result.get().getStatus(), TaskStatus.ARCHIVED);
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method createTask => test if the endpoint has a expected behavior and is secured")
    void createTask() {
        var request = new RequestTaskDto(
                "Task 1",
                "Description 1",
                LocalDateTime.now().plusDays(1),
                TaskStatus.TODO,
                TaskPriority.LOW
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/tasks")
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/tasks")
                .then()
                .statusCode(201);

        var list = taskRepository.findAll();
        assertEquals(1, list.size());

        var savedTask = list.getFirst();

        assertEquals("Task 1", savedTask.getTitle());
        assertEquals("Description 1", savedTask.getDescription());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertEquals(TaskPriority.LOW, savedTask.getPriority());
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getCreatedBy());
        assertNull(savedTask.getLastModifiedAt());
        assertNull(savedTask.getLastModifiedBy());
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method updateTask => test if the endpoint has a expected behavior and is secured")
    void updateTask() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        var savedTask = taskRepository.save(task);

        var request = new RequestTaskDto(
                "Task 2",
                "Description 2",
                LocalDateTime.now().plusDays(2),
                TaskStatus.COMPLETED,
                TaskPriority.MEDIUM
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/tasks/" + savedTask.getId())
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put("/api/v1/tasks/" + savedTask.getId())
                .then()
                .statusCode(202);

        var resultOptional = taskRepository.findById(savedTask.getId());

        assertTrue(resultOptional.isPresent());

        var result = resultOptional.get();

        assertEquals("Task 2", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getLastModifiedAt());
        assertNotNull(result.getLastModifiedBy());
    }

    @Test
    @WithMockUser(username = "service-account-taskmaster-client")
    @DisplayName("Controller method createTaskComment => test if the endpoint has a expected behavior and is secured")
    void createTaskComment() {
        Task task = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .dueDate(LocalDateTime.now().plusDays(1))
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        var savedTask = taskRepository.save(task);

        var request = new RequestTaskCommentDto("Comment 1");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/tasks/" + savedTask.getId() + "/comments")
                .then()
                .statusCode(401);

        String token = getToken();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/tasks/" + savedTask.getId() + "/comments")
                .then()
                .statusCode(201);


        var comments = taskCommentRepository.findByTask_Id(savedTask.getId());


        assertEquals(1, comments.size());
        assertEquals("Comment 1", comments.getFirst().getComment());
        assertNotNull(comments.getFirst().getCreatedAt());
        assertNotNull(comments.getFirst().getCreatedBy());
        assertNotNull(comments.getFirst().getId());
    }

    private String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", singletonList(GRANT_TYPE));
        map.put("client_id", singletonList(CLIENT_ID));
        map.put("client_secret", singletonList(CLIENT_SECRET));

        String authServerUrl = oAuth2ResourceServerProperties.getJwt().getIssuerUri() + "/protocol/openid-connect/token";

        var request = new HttpEntity<>(map, httpHeaders);
        KeyCloakToken token = restTemplate.postForObject(
                authServerUrl,
                request,
                KeyCloakToken.class
        );

        assert token != null;
        return token.accessToken();
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }
}