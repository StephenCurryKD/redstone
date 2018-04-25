package com.redstone.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redstone.common.pojo.EasyUITreeNode;
import com.redstone.mapper.TbItemCatMapper;
import com.redstone.pojo.TbItemCat;
import com.redstone.pojo.TbItemCatExample;
import com.redstone.pojo.TbItemCatExample.Criteria;
import com.redstone.service.ItemCatService;

/**
 * 商品分类管理
 * <p>Title:ItemCatServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @date 2017年12月9日
 * @version V1.0
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	public TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EasyUITreeNode> getItemCatList(long parentId) {
		//根据父节点id查询子节点列表
		TbItemCatExample catExample=new TbItemCatExample();
		//设置查询条件
		Criteria criteria = catExample.createCriteria();
		//设置parentid
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbItemCat> list = itemCatMapper.selectByExample(catExample);
		//转成easyuitreenode
		List<EasyUITreeNode> resultList=new ArrayList<>();
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			//如果节点下面有子节点,则为"closed"否则为"open"
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			//添加到节点列表
			resultList.add(node);
		}
		return resultList;
	}

}
