package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStaEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 秒杀 业务逻辑层
 * 
 * @author 甘海彬
 *
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {
	// 日志
	private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao redisDao;
	//md5 密钥
	private final String slat = "antgan";
	
	/**
	 * 获取秒杀列表
	 */
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	/**
	 * 获取秒杀列表 通过 id
	 */
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	/**
	 * 获取秒杀url
	 */
	public Exposer exportSeckillUrl(long seckillId) {
		//先从缓存获取seckill
		Seckill seckill = redisDao.getSeckill(seckillId);
		
		if(seckill==null){
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false, seckillId);
			}else{
				//放入缓存中
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime() || 
				nowTime.getTime() > endTime.getTime() ){
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	/**
	 * 秒杀操作
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SecurityException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		
		//先执行插入明细操作，再进行减库存
		try {
			Date nowTime  = new Date();
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if(insertCount <= 0){
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			}else{
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(updateCount <=0 ){
					throw new SeckillCloseException("seckill is closed");
				}else{
					//秒杀成功
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStaEnum.SUCCESS, successKilled);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			//编译异常，转成运行时异常。这样才能rollback
			throw new SeckillException("seckill inner error" + e.getMessage());
		}
	}
	
	/**
	 * 通过存储过程来实现秒杀操作
	 */
	public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		
		try {
			seckillDao.killByProcedure(map);
			int result = MapUtils.getInteger(map, "result", -2);
			//秒杀成功
			if(result == 1){
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStaEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId, SeckillStaEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStaEnum.INNER_ERROR);
		}
	}
	
	//MD5加密
	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
}
