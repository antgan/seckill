package org.seckill.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
	@Autowired
	private RedisDao redisDao;
	@Autowired
	private SeckillDao seckillDao;
	
	@Test
	public void testDao() {
		long id = 1001;
		Seckill s = redisDao.getSeckill(id);
		if(s ==null){
			s = seckillDao.queryById(id);
			if(s !=null){
				String result = redisDao.putSeckill(s);
				System.out.println(result);
				s = redisDao.getSeckill(id);
				System.out.println(s.toString());
			}
		}else{
			System.out.println(s.toString());
		}
		
		
	}
}
