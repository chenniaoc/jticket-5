package com.jiepang.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.jiepang.model.Order;
import com.jiepang.model.Ticket;
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
		initTestTickets(); // TODO
	}

	public String test(String setVal) {
		opsForValue.set("test", setVal);
		return opsForValue.get("test");
	}

	public void initTestTickets() {
		String tno = "T10";
		String date = "20121120";

		BoundZSetOperations<String, String> zsetOps = template
				.boundZSetOps(KeyUtils.remain(tno, date));

		for (int i = 1; i < 201; i++) {
			zsetOps.add(String.valueOf(i), i);
		}
	}

	public List<Ticket> getRemainTicketsByNoAndDate(String trainNo, String date) {
		List<Ticket> result = new ArrayList<Ticket>();

		BoundZSetOperations<String, String> zsetOps = template
				.boundZSetOps(KeyUtils.remain(trainNo, date));

		Set<String> seatSet = zsetOps.range(0, -1);
		for (String seatNo : seatSet) {
			Ticket t = new Ticket();
			t.setDate(date);
			t.setSeatNo(seatNo);
			t.setTrainNo(trainNo);
			result.add(t);
		}
		return result;
	}

	public String removeOrder(final Order o) {
		String msg = "";
		final String orderNo = o.getOrderNo();
		final String[] uids = o.getIdentifyNo();
		final List<String> watchKeyList = new ArrayList<String>();
		final String key;
		final String trainNo;
		final String date;
		Set<String> keys = template.keys(KeyUtils.order(uids[0], "*"));
		if (keys != null && keys.size() > 0) {
			key = keys.iterator().next();
		} else {
			return "You hava not reserved any tickets,please reserve first!"
					+ uids[0];
		}
		System.out.println(key);
		String[] splitedOrderKey = KeyUtils.getSplitOrderKey(key);
		trainNo = splitedOrderKey[1];

		// 查询是否已经有定过票
		for (String uid : o.getIdentifyNo()) {
			if (template.hasKey(KeyUtils.order(uid, "*"))) {
				return "You hava not reserved any tickets,please reserve first!"
						+ uid;
			}
			watchKeyList.add(KeyUtils.order(uid, "*"));
		}

		msg = template.execute(new SessionCallback<String>() {

			@Override
			public String execute(RedisOperations ops)
					throws DataAccessException {
				// TODO Auto-generated method stub
				HashOperations<String, String, String> opsForHash = ops
						.opsForHash();
				String[] seatNoarray = new String[uids.length];
				String _orderNo = "";
				String date = "";
				for (int i = 0; i < uids.length; i++) {
					String order_key = KeyUtils.order(uids[i], trainNo);
					// BoundHashOperations<String, String, String> orderOps =
					// ops
					// .boundHashOps(order_key);
					// opsForHash.hasKey(order_key, date)
					if (!opsForHash.hasKey(order_key, "date")
							|| !opsForHash.hasKey(order_key, "seatNo")) {
						return "You hava not reserved any tickets,please reserve first!"
								+ uids[i];
					}

					String seatNo = opsForHash.get(order_key, "seatNo");
					date = opsForHash.get(order_key, "date");
					_orderNo = opsForHash.get(order_key, "orderNo");
					seatNoarray[i] = seatNo;
					watchKeyList.add(order_key);
				}
				if (_orderNo == null || !_orderNo.equals(orderNo)) {
					return "the orderNo you inputed was not correct,please confirm it."
							+ orderNo;
				}
				// start tx
				ops.watch(watchKeyList);

				// modify
				ops.multi();
				BoundZSetOperations<String, String> zsetOps = ops
						.boundZSetOps(KeyUtils.remain(trainNo, date));
				// 把票号返回到余票池
				for (String seatNo : seatNoarray) {
					zsetOps.add(seatNo, Double.parseDouble(seatNo));
				}
				for (int i = 0; i < uids.length; i++) {
					String order_key = KeyUtils.order(uids[i], trainNo);

					BoundHashOperations<String, String, String> cancelOrderOps = ops
							.boundHashOps(KeyUtils
									.cancleOrder(uids[i], trainNo));

					cancelOrderOps.put("date", date);
					cancelOrderOps.put("seatNo", seatNoarray[i]);
					cancelOrderOps.put("orderNo", orderNo);
					ops.delete(order_key);
				}

				// execute
				List result = ops.exec();
				if (result == null) {
					ops.discard();
					return "failed";
				}

				return "success";
			}

		});

		return msg;
	}

	/**
	 * 生成订单
	 * 
	 * @param o
	 * @return 失败返回空
	 */
	public String addOrder(final Order o) {
		final String trainNo = o.getTrainNo();
		final String date = o.getTrainDate();
		final List<String> watchKeyList = new ArrayList<String>();
		// 查询是否已经有定过票
		for (String uid : o.getIdentifyNo()) {
			BoundHashOperations<String, String, String> orderOps = template
					.boundHashOps(KeyUtils.order(uid, trainNo));
			if (template.hasKey(KeyUtils.order(uid, trainNo))) {

				return "You have already reserved this trainNo:" + trainNo
						+ ",orderNo:" + orderOps.get("orderNo");
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
				// start tx
				ops.watch(watchKeyList);
				// read
				BoundZSetOperations<String, String> zsetOps = ops
						.boundZSetOps(KeyUtils.remain(trainNo, date));

				long ticketRemainCount = zsetOps.size();
				// 余票小于订票人数
				if (ticketRemainCount < peopelNo) {

					return "the number of remain tickets is not avaliubale";

				}
				Set<String> seatNo = zsetOps.range(0, peopelNo - 1);
				Object[] seatNoArray = seatNo.toArray();

				System.out.println(KeyUtils.remain(trainNo, date));
				// modify
				ops.multi();
				zsetOps.removeRange(0, peopelNo - 1);
				orderNo = CommonUtils.generateOrderNo();
				for (int i = 0; i < peopelNo; i++) {
					String uid = o.getIdentifyNo()[i];
					System.out.println(KeyUtils.order(uid, trainNo));
					BoundHashOperations<String, String, String> orderOps = ops
							.boundHashOps(KeyUtils.order(uid, trainNo));
					orderOps.put("date", date);
					orderOps.put("orderNo", orderNo);
					orderOps.put("seatNo", (String) seatNoArray[i]);
				}

				// execute
				List result = ops.exec();
				if (result == null) {
					ops.discard();
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
		BoundSetOperations<String, String> setOps = template
				.boundSetOps(KeyUtils.globalUid());
		if (!setOps.isMember(user.getIdentify_no())) {
			setOps.add(user.getIdentify_no());
			result = 1;
		}
		return result;
	}

	public boolean isVaildUser(String uid) {
		boolean result = false;
		BoundSetOperations<String, String> setOps = template
				.boundSetOps(KeyUtils.globalUid());
		if (setOps.isMember(uid)) {
			result = true;
		}
		return result;
	}

}
