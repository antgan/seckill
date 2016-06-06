-- 数据库初始化脚本
create DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表
create table seckill (
	`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name` varchar(120) NOT NULL COMMENT '商品名称',
	`number` int NOT NULL COMMENT '库存数量',
	`start_time` timestamp NOT NULL COMMENT '秒杀开启时间',
	`end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
	PRIMARY KEY (seckill_id),
	key idx_start_time(start_time),
	key idx_end_time(end_time),
	key idx_create_time(create_time)
)engine=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表'

-- 初始化数据
insert into 
	seckill(name, number, start_time, end_time)
values
	('1000元秒杀iphone6',100, '2016-11-01 00:00:00', '2016-11-02 00:00:00'),
	('200元秒杀iphone4',100, '2016-11-01 00:00:00', '2016-11-02 00:00:00'),
	('1000元秒杀iphone5',100, '2016-11-01 00:00:00', '2016-11-02 00:00:00'),
	('2000元秒杀mac',100, '2016-11-01 00:00:00', '2016-11-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
create table success_killed(
	`seckill_id` bigint NOT NULL COMMENT '秒杀商品id',
	`user_phone` bigint NOT NULL COMMENT '用户手机号',
	`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识：-1：无效，0：成功，1：已付款',
	'create_time' timestamp NOT NULL COMMENT '创建时间'，
	PRIMARY key(seckill_id, user_phone), /*联合主键*/
	key idx_create_time (create_time)
)engine=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表'