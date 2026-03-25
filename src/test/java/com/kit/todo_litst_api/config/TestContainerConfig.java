package com.kit.todo_litst_api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

	@Bean
	@ServiceConnection
	public PostgreSQLContainer postgresDBContainer() {
		return new PostgreSQLContainer("postgres:16-alpine");
	}

}
