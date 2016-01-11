package org.seasar.doma.boot;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.springframework.transaction.annotation.Transactional;

@Dao
@ConfigAutowireable
@Transactional
public interface MessageDao {
	@Select
	List<Message> selectAll();

	@Insert
	int insert(Message message);
}
