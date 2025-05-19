package org.seasar.doma.boot.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private TestRestTemplate restTemplate;
	private final ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<List<Message>>() {
	};
	@LocalServerPort
	private int port;

	@Test
	void test() {
		Message message1 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "hello").build().toUri(),
				Message.class);
		assertEquals(1, message1.id);
		assertEquals("hello", message1.text);
		Message message2 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "world").build().toUri(),
				Message.class);
		assertEquals(2, message2.id);
		assertEquals("world", message2.text);

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.build().toUri(),
					HttpMethod.GET, HttpEntity.EMPTY,
					typedReference).getBody();
			assertEquals(2, messages.size());
			assertEquals(message1.id, messages.get(0).id);
			assertEquals(message1.text, messages.get(0).text);
			assertEquals(message2.id, messages.get(1).id);
			assertEquals(message2.text, messages.get(1).text);
		}

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.queryParam("page", "1").queryParam("size", "1").build()
							.toUri(),
					HttpMethod.GET, HttpEntity.EMPTY, typedReference)
					.getBody();
			assertEquals(1, messages.size());
			assertEquals(message2.id, messages.get(0).id);
			assertEquals(message2.text, messages.get(0).text);
		}
	}

}
