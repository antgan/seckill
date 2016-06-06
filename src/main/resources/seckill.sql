-- 秒杀执行存储过程
DELIMITER $$; -- console; 转换成$$ 
-- row_count() 返回上一条修改类型(delete,insert,update)的影响行数
create procedure `seckill`.`execute_seckill`
	(in v_seckill_id bigint, in v_phone bigint, in v_kill_time timestamp, out r_result int)
	begin 
		declare insert_count int default 0;
		start transaction;
		insert ignore into success_killed(seckill_id, user_phone,state ,create_time)
		values(v_seckill_id, v_phone, 0, v_kill_time);
		select row_count() into insert_count;
		if(insert_count = 0) then
			rollback;
			set r_result = -1;-- 重复秒杀
		elseif(insert_count < 0) then
			rollback;
			set r_result = -2; -- 系统异常
		else
			update seckill set number = number - 1 
			where seckill_id = v_seckill_id 
			and end_time > v_kill_time 
			and start_time < v_kill_time 
			and number > 0;
			
			select row_count() into insert_count;
			if(insert_count = 0) then
				rollback;
				set r_result = 0;-- 秒杀结束
			elseif(insert_count < 0) then
				rollback;
				set r_result = -2; -- 系统异常
			else
				commit;
				set r_result = 1;
			end if;
		end if;
	end
$$

-- 存储过程定义结束
DELIMITER ;
set @r_result = -3;
call execute_seckill(1003, 13020301003, now(), @r_result);

select @r_result;
	