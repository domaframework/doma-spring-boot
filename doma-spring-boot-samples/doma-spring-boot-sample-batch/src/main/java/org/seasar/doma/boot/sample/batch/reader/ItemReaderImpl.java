package org.seasar.doma.boot.sample.batch.reader;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

public class ItemReaderImpl<T> implements ItemStreamReader<T> {

	private final Supplier<Stream<T>> supplier;
	private Stream<T> stream;
	private Iterator<T> iterator;

	public ItemReaderImpl(Supplier<Stream<T>> supplier) {
		this.supplier = supplier;
	}

	@Override
	public void open(ExecutionContext executionContext) {
		stream = supplier.get();
		iterator = stream.iterator();
	}

	@Override
	public void update(ExecutionContext executionContext) {
	}

	@Override
	public void close() {
		if (stream != null) {
			stream.close();
		}
	}

	@Override
	public T read() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
