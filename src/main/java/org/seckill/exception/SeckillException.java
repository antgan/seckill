package org.seckill.exception;

/**
 * 秒杀程序异常超类
 * @author ant
 *
 */
public class SeckillException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public SeckillException(String message) {
		super(message);
	}
	
	public SeckillException(String message, Throwable cause) {
		super(message,cause);
	}
}
