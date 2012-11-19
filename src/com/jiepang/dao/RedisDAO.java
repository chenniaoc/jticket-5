package com.jiepang.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.jiepang.model.User;
import com.jiepang.util.KeyUtils;
@Service
public class RedisDAO {
	
	@Autowired
	private final StringRedisTemplate template;
	
	private final ValueOperations<String,String> opsForValue;
	
	@Autowired
	public RedisDAO(StringRedisTemplate template){
		this.template = template;
		opsForValue = this.template.opsForValue();
	}
	
	public String test(String setVal){
		opsForValue.set("test", setVal);
		return opsForValue.get("test");
	}
	
	/**
	 * 
	 * @return
	 */
	public int addUser(User user){
		int result = 0;
		
		return result;
	}
	
	public boolean isVaildUser(String uid){
		boolean result = false;
		
		return result;
	}
	
	public boolean orderTickets(String uid,String ...tickets){
		boolean result = false;
		
		return result;
	}
	
	public boolean returnOrders(String uid,String orderNo,String ...tickets){
		boolean result = false;
		
		return result;
	}
	
	public String getRemainTicketsNo(String tno){
		String result = "0";
		result = opsForValue.get(KeyUtils.remain(tno));
		return result;
	}
}
