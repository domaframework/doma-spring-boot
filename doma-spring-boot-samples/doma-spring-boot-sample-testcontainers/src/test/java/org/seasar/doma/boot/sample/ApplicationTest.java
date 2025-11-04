package org.seasar.doma.boot.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
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
	void withTestContainers() {
		Message message1 = restClient.get()
				.uri("/?text={text}", "hello")
				.retrieve()
				.body(Message.class);
		assertThat(message1.id).isEqualTo(1);
		assertThat(message1.text).isEqualTo("hello");

		Message message2 = restClient.get()
				.uri("/?text={text}", "world")
				.retrieve()
				.body(Message.class);
		assertThat(message2.id).isEqualTo(2);
		assertThat(message2.text).isEqualTo("world");

		{
			List<Message> messages = restClient.get()
					.uri("/")
					.retrieve()
					.body(typedReference);
			assertThat(messages.size()).isEqualTo(2);
			assertThat(messages.get(0).id).isEqualTo(message1.id);
			assertThat(messages.get(0).text).isEqualTo(message1.text);
			assertThat(messages.get(1).id).isEqualTo(message2.id);
			assertThat(messages.get(1).text).isEqualTo(message2.text);
		}

		{
			List<Message> messages = restClient.get()
					.uri("/?page={page}&size={size}", 1, 1)
					.retrieve()
					.body(typedReference);
			assertThat(messages.size()).isEqualTo(1);
			assertThat(messages.get(0).id).isEqualTo(message2.id);
			assertThat(messages.get(0).text).isEqualTo(message2.text);
		}
	}
}
