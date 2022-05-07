package org.seasar.doma.boot.sample.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class SampleApp {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(SampleApp.class, args);
		int exitCode = SpringApplication.exit(applicationContext);
		System.exit(exitCode);
	}
}
