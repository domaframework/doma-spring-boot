package org.seasar.doma.boot.sample;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.transaction.annotation.Transactional;

@Dao
@ConfigAutowireable
@Transactional
public interface MessageDao {
	@Select
	List<Message> selectAll(SelectOptions options);

	@Insert
	int insert(Message message);
}
