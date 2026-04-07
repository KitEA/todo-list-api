package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.dto.TodoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestContainerConfig.class)
class TodoControllerE2ETest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    void shouldRegisterAndCreateTodo() {
        var registerRequest = new RegisterRequest("Kit", "kit@gmail.com", "124879842");

        var registerResponse = restTestClient.post()
                .uri("/api/auth/register")
                .body(registerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        String jwtToken = Optional.ofNullable(registerResponse)
                .map(res -> (String) res.get("token"))
                .orElseThrow(() -> new AssertionError("Token not found in response"));

        var todoRequest = new TodoRequest("Wash Cloth", "");

        restTestClient.post().uri("/api/todos")
                .header("Authorization", "Bearer " + jwtToken)
                .body(todoRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Wash Cloth")
                .jsonPath("$.id").exists();
    }
}
