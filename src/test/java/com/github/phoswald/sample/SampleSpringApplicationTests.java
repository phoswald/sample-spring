package com.github.phoswald.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SampleSpringApplicationTests {

	@Test
	void contextLoads() {
	}

}
