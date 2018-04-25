package com.redstone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.JsonUtils;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbItemDesc;
import com.redstone.service.ItemService;

/**
 * 商品管理Controller
 * <p>Title:ItemController</p>
 * <p>Description:</p>
 * @author sky
 * @date 2017年11月28日
 * @version V1.0
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem tbItem=itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows) {
		EasyUIDataGridResult result=itemService.getItemList(page, rows);
		return result;
	}
	
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public RedStoneResult addItem(TbItem item,String desc) {
		RedStoneResult result = itemService.addItem(item, desc);
		return result;
	}
	
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public RedStoneResult deleteItem(String ids) {
		RedStoneResult result = itemService.deleteItem(ids);
		return result;
	}
	
	@RequestMapping("/rest/item/update")
	@ResponseBody
	public RedStoneResult updateItem(TbItem item,String desc) {
		RedStoneResult result = itemService.updateItem(item,desc);
		return result;
	}
	
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public RedStoneResult getItemDescById(@PathVariable long id) {
		RedStoneResult result = itemService.getItemDescById(id);
		return result;
	}
	
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public RedStoneResult instockItem(String ids) {
		RedStoneResult result = itemService.instockItem(ids);
		return result;
	}
	
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public RedStoneResult reshelfItem(String ids) {
		RedStoneResult result = itemService.reshelfItem(ids);
		return result;
	}
	
	
}
