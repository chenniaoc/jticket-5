package com.jiepang.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.jiepang.model.Order;
import com.jiepang.model.User;
import com.jiepang.util.CommonUtils;
import com.jiepang.util.KeyUtils;

@Service
public class RedisDAO {

	@Autowired
	private final StringRedisTemplate template;

	private final ValueOperations<String, String> opsForValue;

	private final HashOperations<String, String, String> opsForHash;

	@Autowired
	public RedisDAO(StringRedisTemplate template) {
		this.template = template;
		opsForValue = this.template.opsForValue();
		opsForHash = this.template.opsForHash();
	}

	public String test(String setVal) {
		opsForValue.set("test", setVal);
		return opsForValue.get("test");
	}

	/**
	 * 生成订单
	 * 
	 * @param o
	 * @return 失败返回空
	 */
	public String addReserve(final Order o) {
		final String trainNo = o.getTrainNo();
		final String date = o.getTrainDate();
		final List<String> watchKeyList = new ArrayList<String>();
		// 查询是否已经有定过票
		for (String uid : o.getIdentifyNo()) {
			if (template.hasKey(KeyUtils.order(uid, trainNo))) {
				return "You have already reserved this trainNo:" + trainNo;
			}
			watchKeyList.add(KeyUtils.order(uid, trainNo));
		}

		String orderNo = template.execute(new SessionCallback<String>() {

			@Override
			public String execute(RedisOperations ops)
					throws DataAccessException {
				// TODO Auto-generated method stub
				String orderNo = "";
				int peopelNo = o.getIdentifyNo().length;
				watchKeyList.add(KeyUtils.remain(trainNo, date));
				//start tx
				ops.watch(watchKeyList);
				// read
				BoundZSetOperations<String, String> zsetOps = ops
						.boundZSetOps(KeyUtils.remain(trainNo, date));
				
				long ticketRemainCount = zsetOps.size();
				// 余票小于订票人数
				if (ticketRemainCount < peopelNo) {

					//return "the number of remain tickets is not avaliubale";

				}
				Set<String> seatNo = zsetOps.range(0, peopelNo - 1);
				Object[] seatNoArray =  seatNo.toArray();
				
				System.out.println(KeyUtils.remain(trainNo, date));
				// modify
				ops.multi();
				zsetOps.removeRange(0, peopelNo - 1);
				orderNo = CommonUtils.generateOrderNo();
				for(int i = 0 ; i < peopelNo ; i++){
					String uid = o.getIdentifyNo()[i];
					System.out.println(KeyUtils.order(uid, trainNo));
					BoundHashOperations<String,String,String> orderOps = ops.boundHashOps(KeyUtils.order(uid, trainNo));
					orderOps.put("date", date);
					orderOps.put("orderNo", orderNo);
					orderOps.put("seatNo", (String)seatNoArray[i]);
				}
				
				
				
				// execute
				List result = ops.exec();
				if (result == null){
					orderNo = "failed";
				}
				return orderNo;
			}

		});

		return orderNo;
	}

	/**
	 * 
	 * @return
	 */
	public int addUser(User user) {
		int result = 0;

		return result;
	}

	public boolean isVaildUser(String uid) {
		boolean result = false;

		return result;
	}

	public boolean orderTickets(String uid, String... tickets) {
		boolean result = false;

		return result;
	}

	public boolean returnOrders(String uid, String orderNo, String... tickets) {
		boolean result = false;

		return result;
	}

	public String getRemainTicketsNo(String tno) {
		String result = "0";
		// result = opsForValue.get(KeyUtils.remain(tno));
		return result;
	}
}
