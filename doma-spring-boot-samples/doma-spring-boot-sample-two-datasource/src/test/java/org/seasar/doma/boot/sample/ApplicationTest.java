package org.seasar.doma.boot.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationTest {

	@Autowired
	private Application application;

	@Test
	void primary() {
		List<PrimaryMessage> messages = application.primaryMessages();
		assertEquals(1, messages.size());
		PrimaryMessage message = messages.get(0);
		assertNotNull(message.id);
		assertEquals("primary message", message.content);
	}

	@Test
	void secondary() {
		List<SecondaryMessage> messages = application.secondaryMessages();
		assertEquals(1, messages.size());
		SecondaryMessage message = messages.get(0);
		assertNotNull(message.id);
		assertEquals("secondary message", message.content);
	}
}
