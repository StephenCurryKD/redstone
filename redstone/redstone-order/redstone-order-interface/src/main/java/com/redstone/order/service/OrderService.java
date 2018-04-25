package com.redstone.order.service;

import java.util.List;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.order.pojo.OrderInfo;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbOrderItem;

public interface OrderService {

	RedStoneResult createOrder(OrderInfo orderInfo,Long id);
	List<OrderInfo> getMyOrderList(Long id);
}
