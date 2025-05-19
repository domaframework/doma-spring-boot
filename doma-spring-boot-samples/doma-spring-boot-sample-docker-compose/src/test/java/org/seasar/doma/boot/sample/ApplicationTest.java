package org.seasar.doma.boot.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.docker.compose.skip.in-tests=false",
		"doma.dialect=POSTGRES"
})
public class ApplicationTest {

	@Test
	void contextLoads() {
	}
}
