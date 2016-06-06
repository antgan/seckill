package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;

/**
 * 秒杀业务
 * @author ant
 *
 */
public interface SeckillService {

	/**
	 * 获取秒杀货品列表
	 * @return
	 */
	public List<Seckill> getSeckillList();
	
	/**
	 * 获取秒杀货品 by id
	 * @param seckillId
	 * @return
	 */
	public Seckill getById(long seckillId);
	
	/**
	 * 暴露秒杀接口
	 * @param seckillId
	 */
	public Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	public SeckillExecution executeSeckill(long seckillId,long userPhone, String md5) 
			throws SecurityException,RepeatKillException, SeckillCloseException;
	
	/**
	 * 存储过程调用
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	public SeckillExecution executeSeckillByProcedure(long seckillId,long userPhone, String md5);
}
