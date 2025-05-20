package org.seasar.doma.boot.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.seasar.doma.boot.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

	@Autowired
	private SampleService service;

	@Test
	void primary() {
		PrimaryMessage message = service.primaryMessage(1).orElseGet(() -> fail());
		assertThat(message.id).isEqualTo(1);
		assertThat(message.content).isEqualTo("primary message");
	}

	@Test
	void secondary() {
		SecondaryMessage message = service.secondaryMessage(2).orElseGet(() -> fail());
		assertThat(message.id).isEqualTo(2);
		assertThat(message.content).isEqualTo("secondary message");
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
			assertThat(message.id).isEqualTo(10);
			assertThat(message.content).isEqualTo("primary commit");
		}
	}

	@Test
	void rollbackPrimary() {
		{
			PrimaryMessage message = new PrimaryMessage(100, "primary rollback");

			boolean thrownException = true;

			assertThatExceptionOfType(RuntimeException.class)
					.isThrownBy(() -> service.insertPrimary(message, thrownException));
		}

		assertThat(service.primaryMessage(100).isPresent()).isFalse();
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
			assertThat(message.id).isEqualTo(20);
			assertThat(message.content).isEqualTo("secondary commit");
		}
	}

	@Test
	void rollbackSecondary() {
		{
			SecondaryMessage message = new SecondaryMessage(200, "secondary rollback");

			boolean thrownException = true;

			assertThatExceptionOfType(RuntimeException.class)
					.isThrownBy(() -> service.insertSecondary(message, thrownException));
		}

		assertThat(service.secondaryMessage(200).isPresent()).isFalse();
	}
}
