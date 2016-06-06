package org.seckill.enums;

/**
 * 秒杀状态枚举
 * @author ant
 *
 */
public enum SeckillStaEnum {
	SUCCESS(1,"秒杀成功"),
	END(0,"秒杀结束"),
	REPEAT_KILL(-1, "重复秒杀"),
	INNER_ERROR(-2,"内部错误"),
	DATA_REWRITE(-3,"数据重写");
	
	private int state;
	private String stateInfo;
	
	SeckillStaEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}
	
	public static SeckillStaEnum stateOf(int index){
		for(SeckillStaEnum state:values()){
			if(state.getState() == index){
				return state;
			}
		}
		return null;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	
	
}
