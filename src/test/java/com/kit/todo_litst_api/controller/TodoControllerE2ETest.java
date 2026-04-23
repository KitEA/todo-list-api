package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.AuthResponse;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Objects;

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
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        String jwtToken = Objects.requireNonNull(registerResponse).token();

        var todoRequest = new TodoRequest("Wash Cloth", "");

        var todoResponse = restTestClient.post().uri("/api/todos")
                .header("Authorization", "Bearer " + jwtToken)
                .body(todoRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TodoResponse.class)
                .returnResult()
                .getResponseBody();

        Long todoId = Objects.requireNonNull(todoResponse).id();

        var updateRequest = new TodoRequest("Wash Cloth (Updated)", "Added soap and water");

        restTestClient.put().uri("/api/todos/{id}", todoId)
                .header("Authorization", "Bearer " + jwtToken)
                .body(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Wash Cloth (Updated)")
                .jsonPath("$.description").isEqualTo("Added soap and water");

        restTestClient.get().uri("/api/todos?page=1&limit=5")
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(1)
                .jsonPath("$.limit").isEqualTo(5)
                .jsonPath("$.total").isEqualTo(1)
                .jsonPath("$.data[0].id").isEqualTo(todoId.intValue());

        restTestClient.delete().uri("/api/todos/{id}", todoId)
                .header("Authorization", "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isNoContent();
    }
}
