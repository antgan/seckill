/**
 * javascript模块化
 */
var seckill = {
	//封装，秒杀相关额ajax url
	URL : {
		now : function(){
			return 'http://localhost:8080/seckill/seckill/time/now';
		},
		exposer : function(seckillId){
			return 'http://localhost:8080/seckill/seckill/'+seckillId+"/exposer";
		},
		execution : function(seckillId, md5){
			return 'http://localhost:8080/seckill/seckill/'+seckillId+"/"+md5+"/"+"execution";
		}
	},
	//处理秒杀逻辑
	handleSeckill : function(seckillId, node){
		node.hide().html("<button class='btn btn-primary btn-lg' id='killBtn' >开始秒杀</button>");
		//发送post请求拿到秒杀地址
		$.post(seckill.URL.exposer(seckillId), {}, function(result){
			//回调方法
			if(result && result['success']){
				var exposer = result['data'];
				if(exposer['exposed']){
					//开启秒杀
					//获取秒杀地址
					var killUrl = seckill.URL.execution(seckillId, exposer['md5']);
					console.log("killUrl:"+killUrl);
					//绑定一次点击事件
					$("#killBtn").one('click',function(){
						//执行秒杀请求操作
						$(this).addClass('disabled');//禁用按钮
						//发送请求
						$.post(killUrl,{},function(result){
							if(result && result['success']){
								var killResult = result['data'];
								var state = killResult['state'];
								var stateInfo = killResult['stateInfo'];
								//显示秒杀结果
								if(state == 1  )
									node.html("<span class='label label-success'>"+stateInfo+"</span>")
								else
									node.html("<span class='label label-danger'>"+stateInfo+"</span>")
							}
						});
					});
					node.show();
				}else{
					//未开启秒杀
					var nowTime = exposer['now'];
					var startTime = exposer['start'];
					var endTime = exposer['end'];
					seckill.countdown(seckillId, nowTime, startTime, endTime);
				}
			}else{
				consolg.log('result:'+result);
			}
		})
	},
	//验证手机号
	validatePhone : function(phone){
		if(phone && phone.length == 11 && !isNaN(phone)){
			return true;
		}else
			return false;
	},
	//倒计时
	countdown:function(seckillId, nowTime, startTime, endTime){
		var seckillBox = $("#seckill-box");
		//时间判断
		if(nowTime > endTime ){
			//秒杀结束
			seckillBox.html('秒杀结束');
		}else if(nowTime < startTime){
			//秒杀未开始
			var killTime = new Date(startTime+1000);
			seckillBox.countdown(killTime, function(event){
				var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
				seckillBox.html(format);
			}).on("finish.countdown",function(){
				//获取秒杀地址，控制显示逻辑
				seckill.handleSeckill(seckillId, seckillBox);
			});
		}else{
			//秒杀开始
			seckill.handleSeckill(seckillId, seckillBox);
		}
	},
	//秒杀逻辑
	detail:{
		//详情页初始化
		init : function(params){
			//手机验证和登录,计时交互
			var killPhone = $.cookie('killPhone');
			if(!seckill.validatePhone(killPhone)){
				//绑定phone
				var killPhoneModal = $("#killPhoneModal");
				killPhoneModal.modal({
					show:true,//显示模态框
					backdrop:'static',//禁止位置关闭
					keyboard:false
				});
				$("#killPhoneBtn").click(function(){
					var phone = $("#killPhoneKey").val();
					if(seckill.validatePhone(phone)){
						$.cookie("killPhone", phone, {expires:7,path:'seckill'});
						window.location.reload();
					}else{
						$("#killPhoneMessage").hide().html("<label class='label label-danger'>手机号格式错误</label>").show(300);
					}
				});
			}
			//已经登录
			var startTime = params['startTime'];
			var endTime = params['endTime'];
			var seckillId = params['seckillId'];
			
			$.get(seckill.URL.now(), {}, function(result){
				if(result && result['success']){
					var nowTime = result['data'];
					//时间判断
					seckill.countdown(seckillId, nowTime, startTime, endTime);
					
				}else{
					consolg.log("result: "+result);
				}
			})
		}
	}
};

