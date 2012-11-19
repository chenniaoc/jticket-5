package com.jiepang.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiepang.dao.RedisDAO;
import com.jiepang.model.Order;


@Controller
public class TicketController {
	@Autowired
	private final RedisDAO redisDAO ;
	
	@Autowired
	public TicketController(RedisDAO redisDAO){
		this.redisDAO = redisDAO;
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/",method=RequestMethod.GET)
	public @ResponseBody Map root(@RequestParam String name) {
		System.out.println("2222");
		List list = new ArrayList();
		for (int i = 0 ; i < ("HelloWorld" + name).length();i++){
			list.add(("HelloWorld" + name).charAt(i));
		}
		Map map = new HashMap();
		map.put("name", name);
		map.put("result",redisDAO.test(name));
		
		return map;
	}
	
	
	
	@RequestMapping(value="/signup",method=RequestMethod.GET)
	public @ResponseBody Map signUpAccount(@RequestParam String[] names) {
		Map map = new HashMap();

		for (String n : names){
			System.out.println(n);
		}
		
		return map;
	}
	
	@RequestMapping(value="/reserve",method=RequestMethod.GET)
	public @ResponseBody Map bookTicket(@RequestParam String[] uids,String date,String TrainNo,int numerOfTicket) {
		Map map = new HashMap();
		Order order = new Order();
		order.setIdentifyNo(uids);
		order.setTrainDate(date);
		order.setTrainNo(TrainNo);
		String msg = redisDAO.addReserve(order);
		
		map.put("message", msg);
		return map;
	}
	
	@RequestMapping(value="/return",method=RequestMethod.GET)
	public @ResponseBody Map returnTicket(@RequestParam String name) {
		Map map = new HashMap();
		
		return map;
	}
	
}
