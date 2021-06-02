package org.seasar.doma.boot.sample;

import java.util.List;

import org.seasar.doma.boot.sample.dao.PrimaryDao;
import org.seasar.doma.boot.sample.dao.SecondaryDao;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private PrimaryDao primaryDao;
	@Autowired
	private SecondaryDao secondaryDao;

	public List<PrimaryMessage> primaryMessages() {
		return primaryDao.selectAll();
	}

	public List<SecondaryMessage> secondaryMessages() {
		return secondaryDao.selectAll();
	}
}
