package org.seasar.doma.boot.sample;

import org.seasar.doma.*;

@Entity(metamodel = @Metamodel)
@Table(name = "messages")
public record Message(
		@Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id,
		String text) {
}
