package org.seasar.doma.boot.sample.service;

import java.util.Optional;

import org.seasar.doma.boot.sample.dao.PrimaryDao;
import org.seasar.doma.boot.sample.dao.SecondaryDao;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleService {

	private final PrimaryDao primaryDao;
	private final SecondaryDao secondaryDao;

	public SampleService(PrimaryDao primaryDao, SecondaryDao secondaryDao) {
		this.primaryDao = primaryDao;
		this.secondaryDao = secondaryDao;
	}

	public Optional<PrimaryMessage> primaryMessage(Integer id) {
		return primaryDao.selectById(id);
	}

	public Optional<SecondaryMessage> secondaryMessage(Integer id) {
		return secondaryDao.selectById(id);
	}

	public void insert(PrimaryMessage primaryMessage, SecondaryMessage secondaryMessage,
			boolean thrownException) {
		primaryDao.insert(primaryMessage);
		secondaryDao.insert(secondaryMessage);
		if (thrownException) {
			throw new RuntimeException();
		}
	}
}
