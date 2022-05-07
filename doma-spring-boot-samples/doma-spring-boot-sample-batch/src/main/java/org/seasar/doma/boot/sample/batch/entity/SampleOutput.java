package org.seasar.doma.boot.sample.batch.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

@Entity(immutable = true)
public class SampleOutput {

	@Id
	public final Integer id;

	public final String content;

	public SampleOutput(Integer id, String content) {
		this.id = id;
		this.content = content;
	}
}
