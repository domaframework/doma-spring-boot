package org.seasar.doma.boot.sample.batch.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.boot.sample.batch.entity.SampleOutput;
import org.seasar.doma.jdbc.BatchResult;

@Dao
@ConfigAutowireable
public interface SampleOutputDao {

	@BatchInsert
	BatchResult<SampleOutput> batchInsert(List<SampleOutput> entities);
}
