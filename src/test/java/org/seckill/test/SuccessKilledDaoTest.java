package org.seckill.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	
	@Test
	public void testInsertSuccessKilled() {
		long id = 1001L;
		long userPhone = 1301030025L;
		int result = successKilledDao.insertSuccessKilled(id, userPhone);
		System.out.println(result);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id = 1000L;
		long userPhone = 1301030025L;
		SuccessKilled s = successKilledDao.queryByIdWithSeckill(id, userPhone);
		System.out.println(s);
		System.out.println(s.getSeckill().toString());
	}

}
