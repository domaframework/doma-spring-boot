package org.seasar.doma.boot.sample.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;

@Dao
@ConfigAutowireable
public interface PrimaryDao {

	@Select
	List<PrimaryMessage> selectAll();
}
