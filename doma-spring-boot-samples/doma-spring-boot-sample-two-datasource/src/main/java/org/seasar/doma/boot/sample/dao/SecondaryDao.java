package org.seasar.doma.boot.sample.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.sample.annotation.SecondaryConfigAutowireable;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;

@Dao
@SecondaryConfigAutowireable
public interface SecondaryDao {

	@Select
	List<SecondaryMessage> selectAll();
}
