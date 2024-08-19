plugins {
	java
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "ro.developmentfactory"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springAiVersion"] = "1.0.0-M1"

dependencies {
	// Spring Boot starters
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-logging")

	// Spring AI Azure
	implementation("org.springframework.ai:spring-ai-azure-store-spring-boot-starter")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// MySQL
	runtimeOnly("com.mysql:mysql-connector-j")

	// Testare
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito:mockito-junit-jupiter")
	testImplementation ("org.junit.jupiter:junit-jupiter")
	testImplementation ("org.testcontainers:testcontainers")
	testImplementation ("org.testcontainers:junit-jupiter")
	testImplementation ("org.testcontainers:testcontainers")
	testImplementation ("org.testcontainers:junit-jupiter")
	testImplementation ("org.testcontainers:mysql")
}


dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
		mavenBom ("org.testcontainers:testcontainers-bom:1.17.6")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
}

