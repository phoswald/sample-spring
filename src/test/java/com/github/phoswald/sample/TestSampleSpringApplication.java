package com.github.phoswald.sample;

import org.springframework.boot.SpringApplication;

public class TestSampleSpringApplication {

	public static void main(String[] args) {
		SpringApplication.from(SampleSpringApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
