package com.redstone.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.CookieUtils;
import com.redstone.common.utils.JsonUtils;
import com.redstone.order.pojo.OrderInfo;
import com.redstone.order.service.OrderService;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbOrderItem;
import com.redstone.pojo.TbUser;

/**
 * 订单确认页面处理Controller
 * <p>Title:OrderCartController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class OrderCartController {

	
	@Value("${CART_KEY}")
	private String CART_KEY;
	@Value("${CART_EXPIRE}")
	private Integer CART_EXPIRE;
	@Autowired
	private OrderService orderService;
	/**
	 * 展示订单确认页面
	 * <p>Title: showOrderCart</p> 
	 * <p>Description:</p>  
	 * @return String
	 * @param request
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		//用户必须是登录状态
		//取用户id
		 TbUser user= (TbUser) request.getAttribute("user");
		 System.out.println(user.getUsername());
		//根据用户信息取收货地址列表,使用静态数据模拟
		//把收货地址列表取出，传递给页面
		//从cookie中取购物车商品列表展示到页面
		List<TbItem> cartItemList = getCartItemList(request);
		//返回逻辑视图
		request.setAttribute("cartList", cartItemList);
		return "order-cart";
	}
	
	/**
	 * 订单生成
	 * <p>Title: createOrder</p> 
	 * <p>Description:</p>  
	 * @return String
	 * @param orderInfo
	 * @return
	 */
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,Model model,HttpServletRequest request) {
		//取用户id
		 TbUser user= (TbUser) request.getAttribute("user");
		 Long id=user.getId();
		//生成订单
		RedStoneResult result = orderService.createOrder(orderInfo,id);
		//返回逻辑视图
		model.addAttribute("orderId", result.getData().toString());
		model.addAttribute("payment", orderInfo.getPayment());
		//预计送达时间，预计三天后送达
		DateTime dateTime=new DateTime();
		dateTime =dateTime.plusDays(3);
		model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
		//返回逻辑视图
		return "success";
	}
	
	@RequestMapping("/order/myorder")
	public String getMyOrderList(HttpServletRequest request) {
		//取用户id
		 TbUser user= (TbUser) request.getAttribute("user");
		//根据用户id取到订单信息中的商品列表
		 Long id = user.getId();
		  List<OrderInfo> orderList = orderService.getMyOrderList(id);
		  //返回逻辑视图
		  request.setAttribute("orderList", orderList);
		return "order-my";
	}
	
	
	
	private List<TbItem> getCartItemList(HttpServletRequest request){
		//从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true);
		if (StringUtils.isBlank(json)) {
			//如果没有内容，则返回一个空列表
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	
}
