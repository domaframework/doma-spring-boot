package org.seasar.doma.boot.sample;

import org.springframework.boot.SpringApplication;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main)
				.with(TestContainersConfig.class)
				.run(args);
	}
}
