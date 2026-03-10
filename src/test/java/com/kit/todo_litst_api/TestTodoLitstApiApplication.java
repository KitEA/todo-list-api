package com.kit.todo_litst_api;

import org.springframework.boot.SpringApplication;

public class TestTodoLitstApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TodoLitstApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
