package com.redstone.order.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.jedis.JedisClient;
import com.redstone.mapper.TbOrderItemMapper;
import com.redstone.mapper.TbOrderMapper;
import com.redstone.mapper.TbOrderShippingMapper;
import com.redstone.order.pojo.OrderInfo;
import com.redstone.order.service.OrderService;
import com.redstone.pojo.TbOrder;
import com.redstone.pojo.TbOrderExample;
import com.redstone.pojo.TbOrderExample.Criteria;
import com.redstone.pojo.TbOrderItem;
import com.redstone.pojo.TbOrderItemExample;
import com.redstone.pojo.TbOrderShipping;

/**
 * 订单处理Service
 * <p>Title:OrderServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	@Autowired
	private JedisClient jedisClient;
	@Value("${ORDER_ID_GEN}")
	private String ORDER_ID_GEN;
	@Value("${ORDER_ID_BEGIN_VALUE}")
	private String ORDER_ID_BEGIN_VALUE;
	@Value("${ORDER_ITEM_ID_GEN}")
	private String ORDER_ITEM_ID_GEN;
	
	@Override
	public RedStoneResult createOrder(OrderInfo orderInfo,Long id) {
		//生成订单号,可以使用redis的incr生成
		if (!jedisClient.exists(ORDER_ID_GEN)) {
			//设置初始值
			jedisClient.set(ORDER_ID_GEN, ORDER_ID_BEGIN_VALUE);
		}
		String orderId = jedisClient.incr(ORDER_ID_GEN).toString();
		//向订单表插入数据,需要补全pojo属性
		orderInfo.setOrderId(orderId);
		//免邮费
		orderInfo.setPostFee("0");
		orderInfo.setUserId(id);
		//1.未付款 2.已付款 3.未发货 4.已发货 5.交易成功 6.交易关闭
		orderInfo.setStatus(1);
		//订单创建时间
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//订单userId
		
		//向订单表插入数据
		orderMapper.insert(orderInfo);
		//向订单明细表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			//获得明细主键
			String oid=jedisClient.incr(ORDER_ITEM_ID_GEN).toString();
			tbOrderItem.setId(oid);
			tbOrderItem.setOrderId(orderId);
			//插入明细数据
			orderItemMapper.insert(tbOrderItem);
		}
		//向订单物流表插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		//返回订单号
		return RedStoneResult.ok(orderId);
	}

	@Override
	public List<OrderInfo> getMyOrderList(Long id) {
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(id);
		List<TbOrder> list = orderMapper.selectByExample(example);
		SimpleDateFormat sfDateFormat=new SimpleDateFormat("yyyyMMddHHss");
		List<OrderInfo> orderInfos=new ArrayList<>();
		for (TbOrder tbOrder : list) {
			OrderInfo orderInfo=new OrderInfo();
			String orderId = tbOrder.getOrderId();
			TbOrderItemExample eItemExample=new TbOrderItemExample();
			com.redstone.pojo.TbOrderItemExample.Criteria criteria2 = eItemExample.createCriteria();
			criteria2.andOrderIdEqualTo(orderId);
			List<TbOrderItem> itemList = orderItemMapper.selectByExample(eItemExample);
			orderInfo.setOrderItems(itemList);
			orderInfo.setOrderId(orderId);
			String format = sfDateFormat.format(tbOrder.getCreateTime());
			Date date=null;
			try {
				date = sfDateFormat.parse(format);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			orderInfo.setCreateTime(date);
			orderInfo.setPayment(tbOrder.getPayment());
			orderInfos.add(orderInfo);
		}
		return orderInfos;
	}

	
	
}





















