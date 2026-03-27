package com.kit.todo_litst_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.kit.todo_litst_api.config.TestContainerConfig;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfig.class)
class TodoLitstApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
