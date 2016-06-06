package org.seckill.test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		{"classpath:spring/spring-dao.xml",
		"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() {
		List<Seckill> list  = seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetById() {
		long id= 1000;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}",seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long id = 1000L;
		Exposer e = seckillService.exportSeckillUrl(id);
		logger.info("exposer={}",e.toString());
	}

	@Test
	public void testExecuteSeckill() {
		long id = 1000L;
		long phone = 10100010L;
		Exposer e = seckillService.exportSeckillUrl(id);
		if(e.isExposed()){
			String md5 = e.getMd5();
			SeckillExecution se = seckillService.executeSeckillByProcedure(id, phone, md5);
			logger.info(se.getStateInfo());
		}
		
		
		
	}

}
