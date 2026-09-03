package com.github.phoswald.sample;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;

public class TestSampleSpringApplication {

	public static void main(String[] args) {
		SpringApplication.from(SampleSpringApplication::main).with(TestcontainersConfiguration.class).run(disableDockerCompose(args));
	}

	/**
	 * Testcontainers provides Postgres here, so the Docker Compose stack configured in
	 * application.properties must stay out of the way: both would contribute connection details.
	 */
	private static String[] disableDockerCompose(String[] args) {
		String[] result = Arrays.copyOf(args, args.length + 1);
		result[args.length] = "--spring.docker.compose.enabled=false";
		return result;
	}

}
