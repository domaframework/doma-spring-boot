package org.seasar.doma.boot.sample;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DomaBootSampleSimpleApplicationTest {
	TestRestTemplate restTemplate = new TestRestTemplate();
	ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<List<Message>>() {
	};
	@Value("${local.server.port}")
	int port;

	@Test
	public void test() {
		Message message1 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "hello").build().toUri(),
				Message.class);
		assertThat(message1.id, is(1));
		assertThat(message1.text, is("hello"));
		Message message2 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "world").build().toUri(),
				Message.class);
		assertThat(message2.id, is(2));
		assertThat(message2.text, is("world"));

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.build().toUri(),
					HttpMethod.GET, HttpEntity.EMPTY,
					typedReference).getBody();
			assertThat(messages.size(), is(2));
			assertThat(messages.get(0).id, is(message1.id));
			assertThat(messages.get(0).text, is(message1.text));
			assertThat(messages.get(1).id, is(message2.id));
			assertThat(messages.get(1).text, is(message2.text));
		}

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.queryParam("page", "1").queryParam("size", "1").build()
							.toUri(),
					HttpMethod.GET, HttpEntity.EMPTY, typedReference)
					.getBody();
			assertThat(messages.size(), is(1));
			assertThat(messages.get(0).id, is(message2.id));
			assertThat(messages.get(0).text, is(message2.text));
		}
	}

}
