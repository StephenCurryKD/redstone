<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
   <meta http-equiv="pragma" content="no-cache">
   <meta http-equiv="cache-control" content="no-cache">
   <meta http-equiv="expires" content="0"> 
   <meta name="format-detection" content="telephone=no">  
   <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"> 
   <meta name="format-detection" content="telephone=no">
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <link rel="stylesheet" href="/css/base.css">
   <link href="/css/purchase.2012.css?v=201410141639" rel="stylesheet" type="text/css">
   <title>我的订单 - 红石商城</title>
   <style type="text/css">
		table.orderListTitleTable {
			border: 1px solid #E8E8E8;
			width: 100%;
			margin: 20px 0px;
			background-color: #f5f5f5;
			text-align: center;
		}
		table.orderListTitleTable td {
			padding: 12px 0px;
		}
		table.orderListItemTable {
			border: 2px solid #ECECEC;
			width: 100%;
			margin: 20px 0px;
		}
		table.orderListItemTable:hover {
			border: 2px solid #aaa !important;
		}
		table.orderListItemTable td {
			padding: 8px 10px;
		}
		tr.orderListItemFirstTR {
			background-color: #F1F1F1;
			font-size:12px;
		}
		td.orderItemProductInfoPartTD {
			border-bottom: solid 1px #ECECEC;
		}
	</style>
   <script>
   	var pageConfig  = {};
   	//日期格式化
   	
   </script>
<body> 
<!--shortcut start-->
<jsp:include page="commons/shortcut.jsp" />
<!--shortcut end-->
<div class="w w1 header clearfix">
	<div id="logo"><a href="http://localhost:8082"><img clstag="clickcart|keycount|xincart|logo" src="/images/redstone-logo.png" title="返回红石商城首页" alt="返回红石商城首页"></a></div>
    <div class="language"><a href="javascript:void(0);" onclick="toEnCart()"></a></div>
	<div class="progress clearfix">
		
	</div>
</div>
<div class="w ">
	<div>
		<h1>我的订单</h1>
	</div>
	<div id="show">
	
		<div class="orderListTitle">
			<table class="orderListTitleTable">
				<tbody>
					<tr>
						<td width="40%" align="center">宝贝</td>
						<td width="10%">单价</td>
						<td width="10%">数量</td>
						<td width="10%">应付款</td>
						<td width="10%">交易操作</td>
					</tr>
				</tbody>
			</table>	
		</div>
	
		<div class="orderListItem">
			<table class="orderListItemTable">
				<tbody>
				 	<c:forEach var="order" items="${orderList}"> 
						<tr class="orderListItemFirstTR">
							<td align="left">订单创建时间：&nbsp;<fmt:formatDate value="${order.createTime }" pattern="yyyy-MM-dd HH:ss"/>
							&nbsp;订单号:&nbsp;${order.orderId }</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<c:forEach var="items" items="${order.orderItems }">
						<tr>
							<td width="40%" align="left">
							<img src="${items.picPath }" width="52px;" height="52px;" />
							${items.title }</td>
							<td width="10%" align="center"><span class="price">¥<fmt:formatNumber groupingUsed="false" value="${items.price / 100}" maxFractionDigits="2" minFractionDigits="2"/></span></td>
							<td width="10%" align="center">${items.num }</td>
							<td width="10%" align="center"><span class="price">¥<fmt:formatNumber groupingUsed="false" value="${items.totalFee / 100 }" maxFractionDigits="2" minFractionDigits="2"/></span></td>
							<td width="10%"></td>
						</tr>
						</c:forEach>
				 	</c:forEach> 
				</tbody>
			</table>	
		</div>

</div>
</div>
<!--推荐位html修改处-->


<script type="text/javascript" src="/js/base-v1.js"></script>
<!-- footer start -->
<jsp:include page="commons/footer.jsp" />
<!-- footer end -->

<!-- 购物车相关业务 -->
<script type="text/javascript" src="/js/cart.js"></script>
<script type="text/javascript" src="/js/jquery.price_format.2.0.min.js"></script>

</html>