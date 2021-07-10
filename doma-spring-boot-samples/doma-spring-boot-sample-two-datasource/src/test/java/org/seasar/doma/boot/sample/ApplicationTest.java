package org.seasar.doma.boot.sample;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.seasar.doma.boot.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationTest {

	@Autowired
	private SampleService service;

	@Test
	void primary() {
		PrimaryMessage message = service.primaryMessage(1).orElseGet(() -> fail());
		assertEquals(1, message.id);
		assertEquals("primary message", message.content);
	}

	@Test
	void secondary() {
		SecondaryMessage message = service.secondaryMessage(2).orElseGet(() -> fail());
		assertEquals(2, message.id);
		assertEquals("secondary message", message.content);
	}

	@Test
	void commitPrimary() {
		{
			PrimaryMessage message = new PrimaryMessage(10, "primary commit");

			boolean thrownException = false;

			service.insertPrimary(message, thrownException);
		}
		{
			PrimaryMessage message = service.primaryMessage(10).orElseGet(() -> fail());
			assertEquals(10, message.id);
			assertEquals("primary commit", message.content);
		}
	}

	@Test
	void rollbackPrimary() {
		{
			PrimaryMessage message = new PrimaryMessage(100, "primary rollback");

			boolean thrownException = true;

			assertThrows(RuntimeException.class,
					() -> service.insertPrimary(message, thrownException));
		}

		assertFalse(service.primaryMessage(100).isPresent());
	}

	@Test
	void commitSecondary() {
		{
			SecondaryMessage message = new SecondaryMessage(20, "secondary commit");

			boolean thrownException = false;

			service.insertSecondary(message, thrownException);
		}
		{
			SecondaryMessage message = service.secondaryMessage(20).orElseGet(() -> fail());
			assertEquals(20, message.id);
			assertEquals("secondary commit", message.content);
		}
	}

	@Test
	void rollbackSecondary() {
		{
			SecondaryMessage message = new SecondaryMessage(200, "secondary rollback");

			boolean thrownException = true;

			assertThrows(RuntimeException.class,
					() -> service.insertSecondary(message, thrownException));
		}

		assertFalse(service.secondaryMessage(200).isPresent());
	}
}
