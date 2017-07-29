package org.seasar.doma.boot;

import java.time.LocalDate;

import org.seasar.doma.boot.event.annotation.HandlePreInsert;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {
	@HandlePreInsert
	public void preInsert(Message message) {
		message.createdAt = LocalDate.now();
	}
}
