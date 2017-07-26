package org.seasar.doma.boot;

import java.time.LocalDate;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.springframework.stereotype.Component;

@Component
public class MessageListener implements EntityListener<Message> {
	@Override
	public void preInsert(Message message, PreInsertContext<Message> context) {
		message.createdAt = LocalDate.now();
	}
}
