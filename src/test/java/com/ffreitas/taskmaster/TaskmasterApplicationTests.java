package com.ffreitas.taskmaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskmasterApplicationTests {

	static final String GRANT_TYPE = "client_credentials";
	static final String CLIENT_ID = "taskmaster-client";
	static final String CLIENT_SECRET = "du8r5RiquFPcA9rWMwifWsV0cBx52S98";

	@Autowired
	private OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @LocalServerPort
    private Integer port;

    @Autowired
    private PostgreSQLContainer<?> container;

	@Autowired
	private KeycloakContainer keycloak;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void databaseContainerHealth() {
        assertTrue(container.isCreated());
        assertTrue(container.isRunning());
    }

	@Test
	void keycloakContainerHealth() {
		assertTrue(keycloak.isCreated());
		assertTrue(keycloak.isRunning());
	}

    @Test
    void contextLoads() {
		String token = getToken();

		given()
				.header("Authorization", "Bearer " + token)
				.contentType("application/json")
				.when()
				.get("/api/v1/management-server/info")
				.then()
				.statusCode(200)
				.body("ApplicationName", equalTo("Taskmaster"))
				.body("Author", equalTo("Francisco Freitas"));
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

	record KeyCloakToken(@JsonProperty("access_token") String accessToken) {}
}
