package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStaEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * controller
 * 
 * @author antgan
 * @datetime 2016/6/6
 *
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
	//日志
	private final Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;
	
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}

	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		if (seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	/**
	 * AJAX, return JSON
	 * 
	 * @param seckillId
	 */
	@RequestMapping(value="/{seckillId}/exposer", method = RequestMethod.POST, produces={"application/json;charset=UTF-8"})
	public @ResponseBody SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result ;
		try{
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false, e.getLocalizedMessage());
		}
		return result;
	}

	@RequestMapping(value="/{seckillId}/{md5}/execution", method= RequestMethod.POST, produces={"application/json;charset=UTF-8"})
	public @ResponseBody SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId, 
			@PathVariable("md5") String md5, 
			@CookieValue(value="killPhone", required=false)/*required=false 可选参数)*/ Long userPhone ){
		
		if(userPhone==null)
			return new SeckillResult<SeckillExecution>(false, "未注册");
		
		try{
			SeckillExecution seckillExecution = seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true, seckillExecution);
		}catch(RepeatKillException e){
			SeckillExecution ex = new SeckillExecution(seckillId, SeckillStaEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true, ex);
		}catch(SeckillCloseException e){
			SeckillExecution ex = new SeckillExecution(seckillId, SeckillStaEnum.END);
			return new SeckillResult<SeckillExecution>(true, ex);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			SeckillExecution ex = new SeckillExecution(seckillId, SeckillStaEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, ex);
		}
	}
	
	@RequestMapping(value="/time/now", method = RequestMethod.GET)
	public @ResponseBody SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}

}
