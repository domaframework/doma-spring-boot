package org.seasar.doma.boot.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTest {
	private RestClient restClient;
	private final ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<>() {
	};
	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp(@Autowired RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder
				.baseUrl("http://localhost:" + port)
				.defaultStatusHandler(__ -> true, (req, res) -> {
				}).build();
	}

	@Test
	void testWithDockerCompose() {
		Message message1 = restClient.get()
				.uri("/?text={text}", "hello")
				.retrieve()
				.body(Message.class);
		assertEquals(1, message1.id);
		assertEquals("hello", message1.text);

		Message message2 = restClient.get()
				.uri("/?text={text}", "world")
				.retrieve()
				.body(Message.class);
		assertEquals(2, message2.id);
		assertEquals("world", message2.text);

		{
			List<Message> messages = restClient.get()
					.uri("/")
					.retrieve()
					.body(typedReference);
			assertEquals(2, messages.size());
			assertEquals(message1.id, messages.get(0).id);
			assertEquals(message1.text, messages.get(0).text);
			assertEquals(message2.id, messages.get(1).id);
			assertEquals(message2.text, messages.get(1).text);
		}

		{
			List<Message> messages = restClient.get()
					.uri("/?page={page}&size={size}", 1, 1)
					.retrieve()
					.body(typedReference);
			assertEquals(1, messages.size());
			assertEquals(message2.id, messages.get(0).id);
			assertEquals(message2.text, messages.get(0).text);
		}
	}
}
