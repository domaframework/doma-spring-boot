package org.seasar.doma.boot;

import java.time.LocalDate;

import org.seasar.doma.boot.event.annotation.DomaEventHandler;
import org.seasar.doma.boot.event.annotation.HandlePreInsert;

@DomaEventHandler
public class MessageHandler {
	@HandlePreInsert
	public void preInsert(Message message) {
		message.createdAt = LocalDate.now();
	}
}
