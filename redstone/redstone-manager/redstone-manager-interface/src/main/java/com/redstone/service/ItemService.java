package com.redstone.service;

import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbItemDesc;

public interface ItemService {

	
	//通过id查询商品信息
	TbItem getItemById(long itemId);
	//分页
	EasyUIDataGridResult getItemList(int page,int rows);
	//添加商品信息
	RedStoneResult addItem(TbItem item,String desc);
	//根据ID查商品描述
	RedStoneResult getItemDescById(long itemId);
	//查商品描述
	TbItemDesc getItemDesc(long itemId); 
	//根据ID删除商品
	RedStoneResult deleteItem(String ids);
	//根据ID修改商品
	RedStoneResult updateItem(TbItem item,String desc);
	//根据ID下架商品
	RedStoneResult instockItem(String ids);
	//根据ID上架商品
	RedStoneResult reshelfItem(String ids);
}
