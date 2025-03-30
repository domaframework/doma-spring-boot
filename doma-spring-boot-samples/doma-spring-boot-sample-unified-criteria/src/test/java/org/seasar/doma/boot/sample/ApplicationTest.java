package org.seasar.doma.boot.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
	private RestClient http;
	private final ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<List<Message>>() {
	};
	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp(@Autowired RestClient.Builder builder) {
		http = builder.baseUrl("http://localhost:" + port).build();
	}

	@Test
	void test() {
		for (var i = 0; i < 10; i++) {
			var text = "message%d".formatted(i);
			http.post().uri("/add").body(Map.of("text", text)).retrieve().body(Message.class);
		}

		{
			List<Message> messages = http.get()
					.retrieve()
					.body(typedReference);
			assertEquals(3, messages.size());
			assertEquals("message0", messages.get(0).text());
			assertEquals("message1", messages.get(1).text());
			assertEquals("message2", messages.get(2).text());
		}

		{
			List<Message> messages = http.get()
					.uri(builder -> builder.queryParam("page", 3).queryParam("size", 2)
							.queryParam("sort", "id,desc").build())
					.retrieve().body(typedReference);
			assertEquals(2, messages.size());
			assertEquals("message3", messages.get(0).text());
			assertEquals("message2", messages.get(1).text());
		}
	}

}
