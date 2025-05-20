package org.seasar.doma.boot.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTest {
	private final TestRestTemplate restTemplate = new TestRestTemplate();
	private final ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<List<Message>>() {
	};
	@LocalServerPort
	private int port;

	@Test
	void test() {
		LocalDate now = LocalDate.now();
		Message message1 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "hello").build().toUri(),
				Message.class);
		assertThat(message1.id).isEqualTo(1);
		assertThat(message1.text).isEqualTo("hello");
		assertThat(message1.createdAt).isEqualTo(now);
		Message message2 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "world").build().toUri(),
				Message.class);
		assertThat(message2.id).isEqualTo(2);
		assertThat(message2.text).isEqualTo("world");
		assertThat(message2.createdAt).isEqualTo(now);

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.build().toUri(),
					HttpMethod.GET, HttpEntity.EMPTY,
					typedReference).getBody();
			assertThat(messages.size()).isEqualTo(2);
			assertThat(messages.get(0).id).isEqualTo(message1.id);
			assertThat(messages.get(0).text).isEqualTo(message1.text);
			assertThat(messages.get(1).id).isEqualTo(message2.id);
			assertThat(messages.get(1).text).isEqualTo(message2.text);
		}

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.queryParam("page", "1").queryParam("size", "1").build()
							.toUri(),
					HttpMethod.GET, HttpEntity.EMPTY, typedReference)
					.getBody();
			assertThat(messages.size()).isEqualTo(1);
			assertThat(messages.get(0).id).isEqualTo(message2.id);
			assertThat(messages.get(0).text).isEqualTo(message2.text);
		}
	}

}
