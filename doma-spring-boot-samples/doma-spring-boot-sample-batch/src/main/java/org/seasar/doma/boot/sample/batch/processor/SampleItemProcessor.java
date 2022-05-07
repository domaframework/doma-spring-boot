package org.seasar.doma.boot.sample.batch.processor;

import org.seasar.doma.boot.sample.batch.entity.SampleInput;
import org.seasar.doma.boot.sample.batch.entity.SampleOutput;
import org.springframework.batch.item.ItemProcessor;

public class SampleItemProcessor implements ItemProcessor<SampleInput, SampleOutput> {

	@Override
	public SampleOutput process(SampleInput item) throws Exception {
		return new SampleOutput(item.id, item.content);
	}
}
