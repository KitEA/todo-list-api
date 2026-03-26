package com.kit.todo_litst_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.kit.todo_litst_api.config.TestContainerConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
class TodoLitstApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
