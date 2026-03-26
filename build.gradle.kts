plugins {
	java
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
}

group = "com.kit"
version = "0.0.1-SNAPSHOT"
description = "Todo app"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.bundles.spring.boot.starters)

	implementation(libs.jjwt.api)
	runtimeOnly(libs.jjwt.impl)
	runtimeOnly(libs.jjwt.jackson)

	compileOnly(libs.lombok)
	runtimeOnly(libs.postgresql)
	annotationProcessor(libs.lombok)

	testImplementation(libs.bundles.spring.boot.starters.test)
	testImplementation(libs.bundles.testcontainers)

	testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
