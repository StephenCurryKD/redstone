package com.redstone.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redstone.item.pojo.Item;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbItemDesc;
import com.redstone.service.ItemService;

/**
 * 商品详情页面展示Controller
 * <p>Title:ItemController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItem(@PathVariable Long itemId,Model model) {
		//取商品基本信息
		TbItem tbItem = itemService.getItemById(itemId);
		Item item=new Item(tbItem);
		//取商品详情
		TbItemDesc itemDesc = itemService.getItemDesc(itemId);
		//把数据传递给页面
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
	
}
















