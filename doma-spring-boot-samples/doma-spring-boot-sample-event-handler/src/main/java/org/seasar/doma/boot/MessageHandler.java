package org.seasar.doma.boot;

import org.seasar.doma.boot.event.annotation.DomaEventHandler;
import org.seasar.doma.boot.event.annotation.HandlePreInsert;

import java.time.LocalDate;

@DomaEventHandler
public class MessageHandler {
	@HandlePreInsert
	public void preInsert(Message message) {
		message.createdAt = LocalDate.now();
	}
}
