/*
 * Copyright (C) 2004-2016 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DomaBootSampleSimpleApplication.class)
@WebIntegrationTest(randomPort = true)
public class DomaBootSampleSimpleApplicationTest {
	RestTemplate restTemplate = new TestRestTemplate();
	ParameterizedTypeReference<List<Message>> typedReference = new ParameterizedTypeReference<List<Message>>() {
	};
	@Value("${local.server.port}")
	int port;

	@Test
	public void test() {
		LocalDate now = LocalDate.now();
		Message message1 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "hello").build().toUri(), Message.class);
		assertThat(message1.id, is(1));
		assertThat(message1.text, is("hello"));
		assertThat(message1.createdAt, is(now));
		Message message2 = restTemplate.getForObject(
				UriComponentsBuilder.fromUriString("http://localhost").port(port)
						.queryParam("text", "world").build().toUri(), Message.class);
		assertThat(message2.id, is(2));
		assertThat(message2.text, is("world"));
		assertThat(message2.createdAt, is(now));

		{
			List<Message> messages = restTemplate.exchange(
					UriComponentsBuilder.fromUriString("http://localhost").port(port)
							.build().toUri(), HttpMethod.GET, HttpEntity.EMPTY,
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
							.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typedReference)
					.getBody();
			assertThat(messages.size(), is(1));
			assertThat(messages.get(0).id, is(message2.id));
			assertThat(messages.get(0).text, is(message2.text));
		}
	}

}
