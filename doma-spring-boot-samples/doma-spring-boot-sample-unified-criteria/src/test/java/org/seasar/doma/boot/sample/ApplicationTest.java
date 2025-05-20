package org.seasar.doma.boot.sample;

import static org.assertj.core.api.Assertions.assertThat;

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
class ApplicationTest {
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
			assertThat(messages.size()).isEqualTo(3);
			assertThat(messages.get(0).text()).isEqualTo("message0");
			assertThat(messages.get(1).text()).isEqualTo("message1");
			assertThat(messages.get(2).text()).isEqualTo("message2");
		}

		{
			List<Message> messages = http.get()
					.uri(builder -> builder.queryParam("page", 3).queryParam("size", 2)
							.queryParam("sort", "id,desc").build())
					.retrieve().body(typedReference);
			assertThat(messages.size()).isEqualTo(2);
			assertThat(messages.get(0).text()).isEqualTo("message3");
			assertThat(messages.get(1).text()).isEqualTo("message2");
		}
	}

}
