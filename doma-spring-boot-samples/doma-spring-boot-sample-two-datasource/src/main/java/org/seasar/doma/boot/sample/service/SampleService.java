package org.seasar.doma.boot.sample.service;

import java.util.Optional;

import org.seasar.doma.boot.sample.dao.PrimaryDao;
import org.seasar.doma.boot.sample.dao.SecondaryDao;
import org.seasar.doma.boot.sample.entity.PrimaryMessage;
import org.seasar.doma.boot.sample.entity.SecondaryMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

	@Transactional(transactionManager = "primaryTransactionManager")
	public void insertPrimary(PrimaryMessage message, boolean thrownException) {
		primaryDao.insert(message);
		if (thrownException) {
			throw new RuntimeException();
		}
	}

	@Transactional(transactionManager = "secondaryTransactionManager")
	public void insertSecondary(SecondaryMessage message, boolean thrownException) {
		secondaryDao.insert(message);
		if (thrownException) {
			throw new RuntimeException();
		}
	}
}
