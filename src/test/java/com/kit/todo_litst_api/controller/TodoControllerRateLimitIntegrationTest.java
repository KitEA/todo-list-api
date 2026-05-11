package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.AuthResponse;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.dto.TodoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestContainerConfig.class)
@ActiveProfiles("rate-limit-test")
class TodoControllerRateLimitIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void shouldThrottleReadRequestsAfterConfiguredLimit() {
        String token = registerAndGetToken("read-limit-user", "read-limit-user@example.com");

        restTestClient.get()
                .uri("/api/todos?page=1&limit=10")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/api/todos?page=1&limit=10")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/api/todos?page=1&limit=10")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isEqualTo(429);
    }

    @Test
    void shouldThrottleWriteRequestsAfterConfiguredLimit() {
        String token = registerAndGetToken("write-limit-user", "write-limit-user@example.com");
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        restTestClient.post()
                .uri("/api/todos")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri("/api/todos")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(429);
    }

    private String registerAndGetToken(String username, String email) {
        var registerResponse = restTestClient.post()
                .uri("/api/auth/register")
                .body(new RegisterRequest(username, email, "password123"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        return Objects.requireNonNull(registerResponse).token();
    }
}
