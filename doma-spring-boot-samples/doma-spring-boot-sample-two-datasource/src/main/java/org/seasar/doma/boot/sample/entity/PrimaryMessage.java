package org.seasar.doma.boot.sample.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

@Entity(immutable = true)
public class PrimaryMessage {

	@Id
	public final Integer id;
	public final String content;

	public PrimaryMessage(Integer id, String content) {
		this.id = id;
		this.content = content;
	}

	@Override
	public String toString() {
		return id + ": " + content;
	}
}
