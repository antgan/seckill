package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * 秒杀明细dao
 * @author ant
 *
 */
public interface SuccessKilledDao {
	/**
	 * 插入秒杀明细
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
	
	/**
	 * 查询秒杀成功明细 携带秒杀商品
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	public SuccessKilled queryByIdWithSeckill( @Param("seckillId")long seckillId, @Param("userPhone") long userPhone);
	
}
