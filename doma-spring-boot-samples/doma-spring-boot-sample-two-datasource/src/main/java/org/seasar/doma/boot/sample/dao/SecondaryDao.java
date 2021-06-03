package org.seasar.doma.boot.sample.dao;

import java.util.Optional;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.sample.annotation.SecondaryConfigAutowireable;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.seasar.doma.jdbc.Result;

@Dao
@SecondaryConfigAutowireable
public interface SecondaryDao {

	@Select
	Optional<SecondaryMessage> selectById(Integer id);

	@Insert
	Result<SecondaryMessage> insert(SecondaryMessage message);
}
