package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

/**
 * 秒杀 dao
 * @author ant
 * 
 */
public interface SeckillDao {

	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	public int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	/**
	 * 查询秒杀商品 by id
	 * @param seckillId
	 * @return
	 */
	public Seckill queryById(long seckillId);
	
	/**
	 * 查询全部秒杀商品
	 * @param offet
	 * @param limit
	 * @return
	 */
	public List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
	
	/**
	 * 使用存储过程执行秒杀
	 * @param paramMap
	 */
	public void killByProcedure(Map<String, Object> paramMap);
}
