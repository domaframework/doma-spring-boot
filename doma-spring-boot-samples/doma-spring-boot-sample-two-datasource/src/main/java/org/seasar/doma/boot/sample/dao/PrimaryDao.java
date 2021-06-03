package org.seasar.doma.boot.sample.dao;

import java.util.Optional;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.jdbc.Result;

@Dao
@ConfigAutowireable
public interface PrimaryDao {

	@Select
	Optional<PrimaryMessage> selectById(Integer id);

	@Insert
	Result<PrimaryMessage> insert(PrimaryMessage message);
}
