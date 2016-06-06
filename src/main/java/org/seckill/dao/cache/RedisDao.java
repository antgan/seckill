package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import ch.qos.logback.classic.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis缓存访问层
 * @author 甘海彬
 *
 */
public class RedisDao {
	private final Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(this.getClass());

	// 类似connection
	private JedisPool jedisPool;
	
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String ip, int port){
		jedisPool = new JedisPool(ip,port);
	}
	
	public Seckill getSeckill(long seckillId){
		//redis逻辑
		try{
			//类似connection
			Jedis jedis = jedisPool.getResource();
			try{
				String key="seckill:"+seckillId;
				//Jedis没内部实现序列化操作
				//流程： get -> byte[] -> 反序列化 -> Object(Seckill)
				// 采用自定义序列化 ，第三方jar,protostuff
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null){
					Seckill seckill = schema.newMessage();//创建空对象
					ProtostuffIOUtil.mergeFrom(bytes,seckill , schema); // 把数据反序列赋值给seckill
					return seckill;
				}
			}finally{
				jedis.close();
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	public String putSeckill(Seckill seckill){
		//流程： Object -> 序列化 ->byte[] -> 发送给redis
		try{
			//类似connection
			Jedis jedis = jedisPool.getResource();
			try{
				String key="seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//超时缓存
				int timeout = 60 * 60;
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			}finally{
				jedis.close();
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
