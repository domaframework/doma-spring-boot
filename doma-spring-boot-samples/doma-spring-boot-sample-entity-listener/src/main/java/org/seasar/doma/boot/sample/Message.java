package org.seasar.doma.boot.sample;

import org.seasar.doma.*;

import java.time.LocalDate;

@Entity(listener = MessageListener.class)
@Table(name = "messages")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;

	public String text;

	public LocalDate createdAt;
}
