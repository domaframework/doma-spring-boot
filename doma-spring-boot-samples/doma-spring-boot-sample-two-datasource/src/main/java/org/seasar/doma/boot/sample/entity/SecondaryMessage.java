package org.seasar.doma.boot.sample.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

@Entity
public class SecondaryMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	public String content;

	@Override
	public String toString() {
		return id + ": " + content;
	}
}
