plugins {
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.mateusznalepa"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("io.micrometer:micrometer-registry-prometheus")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.create("MyFatJar", Jar::class) {
	group = "my tasks" // OR, for example, "build"
	description = "Creates a self-contained fat JAR of the application that can be run."
	manifest.attributes["Main-Class"] = "com.mateusznalepa.http.clients.WebfluxMockServerKt"
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	val dependencies = configurations
		.runtimeClasspath
		.get()
		.map(::zipTree)
	from(dependencies)
	with(tasks.jar.get())
}
