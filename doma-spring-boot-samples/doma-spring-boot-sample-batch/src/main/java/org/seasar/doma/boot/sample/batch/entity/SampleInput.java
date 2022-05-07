package org.seasar.doma.boot.sample.batch.entity;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

@Entity(immutable = true)
public class SampleInput {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public final Integer id;

	public final String content;

	public SampleInput(Integer id, String content) {
		this.id = id;
		this.content = content;
	}
}
