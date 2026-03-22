package com.kit.todo_litst_api.dev;

import com.kit.todo_litst_api.TodoLitstApiApplication;
import com.kit.todo_litst_api.config.TestContainerConfig;
import org.springframework.boot.SpringApplication;

public class LocalDevApplication {
    static void main(String[] args) {
        SpringApplication.from(TodoLitstApiApplication::main)
                .with(TestContainerConfig.class)
                .run(args);
    }
}
