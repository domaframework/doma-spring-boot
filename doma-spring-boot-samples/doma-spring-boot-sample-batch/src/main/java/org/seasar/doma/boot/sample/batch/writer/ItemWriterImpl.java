package org.seasar.doma.boot.sample.batch.writer;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.batch.item.ItemWriter;

public class ItemWriterImpl<T> implements ItemWriter<T> {

	private final Consumer<List<T>> consumer;

	public ItemWriterImpl(Consumer<List<T>> consumer) {
		this.consumer = consumer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends T> items) throws Exception {
		consumer.accept((List<T>) items);
	}
}
