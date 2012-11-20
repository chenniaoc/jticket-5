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
import com.jiepang.model.Ticket;
import com.jiepang.model.User;
import com.jiepang.util.CommonUtils;

@Controller
public class TicketController {
	@Autowired
	private final RedisDAO redisDAO;

	@Autowired
	public TicketController(RedisDAO redisDAO) {
		this.redisDAO = redisDAO;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody
	Map root(@RequestParam String name) {
		System.out.println("2222");
		List list = new ArrayList();
		for (int i = 0; i < ("HelloWorld" + name).length(); i++) {
			list.add(("HelloWorld" + name).charAt(i));
		}
		Map map = new HashMap();
		map.put("name", name);
		map.put("result", redisDAO.test(name));

		return map;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public @ResponseBody
	int signUpAccount(@RequestParam String uid) {

		User user = new User();
		user.setIdentify_no(uid);
		return redisDAO.addUser(user);
	}

	@RequestMapping(value = "/reserve", method = RequestMethod.GET)
	public @ResponseBody
	Map bookTicket(@RequestParam String[] uids, @RequestParam String date,
			@RequestParam String TrainNo, @RequestParam int numerOfTicket) {
		Map map = new HashMap();
		Order order = new Order();

		uids = CommonUtils.unifyParams(uids);

		if (numerOfTicket != uids.length) {
			map.put("message", "numerOfTicket is conflict with passed UIDS");
			return map;
		}

		String msg = "";
		if (verifyUsers(uids)) {
			order.setIdentifyNo(uids);
			order.setTrainDate(date);
			order.setTrainNo(TrainNo);
			msg = redisDAO.addOrder(order);
		} else {

			msg = "uids were not valid";
		}

		map.put("message", msg);
		return map;
	}

	/**
	 * 
	 * @param orderNo
	 *            订单号
	 * @param uids
	 *            身份证列表
	 * @return
	 */
	@RequestMapping(value = "/return", method = RequestMethod.GET)
	public @ResponseBody
	Map returnTicket(@RequestParam String orderNo, @RequestParam String[] uids) {
		Map map = new HashMap();
		Order order = new Order();
		order.setIdentifyNo(CommonUtils.unifyParams(uids));
		order.setOrderNo(orderNo);
		String msg = "";
		if (verifyUsers(uids)) {
			msg = redisDAO.removeOrder(order);
		} else {
			msg = "uids were not valid";
		}
		map.put("message", msg);
		return map;
	}

	/**
	 * 
	 * @param orderNo
	 *            订单号
	 * @param uids
	 *            身份证列表
	 * @return
	 */
	@RequestMapping(value = "/remain", method = RequestMethod.GET)
	public @ResponseBody
	Map returnTicket(@RequestParam String trainNo, @RequestParam String date) {
		Map map = new HashMap();

		List<Ticket> result = redisDAO.getRemainTicketsByNoAndDate(trainNo,
				date);
		map.put("total", result.size());
		map.put("detail", result);
		return map;
	}

	public boolean verifyUsers(String[] uids) {
		boolean result = false;
		for (String uid : uids) {
			if (!redisDAO.isVaildUser(uid)) {
				return false;
			}
			result = true;
		}

		return result;
	}

}
