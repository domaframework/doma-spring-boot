package org.seasar.doma.boot.sample.batch.dao;

import java.util.stream.Stream;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Suppress;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.boot.sample.batch.entity.SampleInput;
import org.seasar.doma.message.Message;

@Dao
@ConfigAutowireable
public interface SampleInputDao {

	@Select
	@Suppress(messages = { Message.DOMA4274 })
	Stream<SampleInput> selectAll();
}
