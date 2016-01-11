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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DomaBootSampleSimpleApplication.class)
@WebIntegrationTest(randomPort = true)
public class DomaBootSampleSimpleApplicationTest {
    RestTemplate restTemplate = new TestRestTemplate();

    @Value("${local.server.port}")
    int port;

    @Test
    public void test() {
        Message message1 = restTemplate.getForObject("http://localhost:" + port
                + "?text=hello", Message.class);
        assertThat(message1.id, is(1));
        assertThat(message1.text, is("hello"));
        Message message2 = restTemplate.getForObject("http://localhost:" + port
                + "?text=world", Message.class);
        assertThat(message2.id, is(2));
        assertThat(message2.text, is("world"));

        List<Message> messages = restTemplate.exchange("http://localhost:"
                        + port, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Message>>() {
                }).getBody();
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0).id, is(message1.id));
        assertThat(messages.get(0).text, is(message1.text));
        assertThat(messages.get(1).id, is(message2.id));
        assertThat(messages.get(1).text, is(message2.text));
    }

}
