package org.seasar.doma.boot.sample.batch.configuration;

import org.seasar.doma.boot.sample.batch.dao.SampleInputDao;
import org.seasar.doma.boot.sample.batch.dao.SampleOutputDao;
import org.seasar.doma.boot.sample.batch.entity.SampleInput;
import org.seasar.doma.boot.sample.batch.entity.SampleOutput;
import org.seasar.doma.boot.sample.batch.processor.SampleItemProcessor;
import org.seasar.doma.boot.sample.batch.reader.ItemReaderImpl;
import org.seasar.doma.boot.sample.batch.writer.ItemWriterImpl;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleBatchConfiguration {

	private final SampleInputDao sampleInputDao;
	private final SampleOutputDao sampleOutputDao;
	private final StepBuilderFactory steps;
	private final JobBuilderFactory jobs;

	public SampleBatchConfiguration(SampleInputDao sampleInputDao, SampleOutputDao sampleOutputDao,
			StepBuilderFactory steps, JobBuilderFactory jobs) {
		this.sampleInputDao = sampleInputDao;
		this.sampleOutputDao = sampleOutputDao;
		this.steps = steps;
		this.jobs = jobs;
	}

	@Bean
	@StepScope
	public ItemReaderImpl<SampleInput> sampleItemReader() {
		return new ItemReaderImpl<>(sampleInputDao::selectAll);
	}

	@Bean
	public SampleItemProcessor sampleItemProcessor() {
		return new SampleItemProcessor();
	}

	@Bean
	public ItemWriterImpl<SampleOutput> sampleItemWriter() {
		return new ItemWriterImpl<>(sampleOutputDao::batchInsert);
	}

	@Bean
	public Step sampleStep() {
		return steps.get("SampleStep")
				.<SampleInput, SampleOutput> chunk(10)
				.reader(sampleItemReader())
				.processor(sampleItemProcessor())
				.writer(sampleItemWriter())
				.build();
	}

	@Bean
	public Job sampleJob() {
		return jobs.get("SampleJob")
				.start(sampleStep())
				.incrementer(new RunIdIncrementer())
				.build();
	}
}
