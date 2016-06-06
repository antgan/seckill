package org.seckill.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * RunWith是为了启动时，加载springIOC容器
 * @author 甘海彬
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
	@Autowired
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById() {
		long id=1000L;
		Seckill s = seckillDao.queryById(id);
		System.out.println(s.getName());
		System.out.println(s.toString());
	}

	@Test
	public void testQueryAll() {
		List<Seckill> list = seckillDao.queryAll(0, 100);
		for(Seckill s : list){
			System.out.println(s.toString());
		}
	}

	@Test
	public void testReduceNumber() {
		long id=1000L;
		int result = seckillDao.reduceNumber(id, new Date());
		System.out.println(result);
	}
	
}
