package com.redstone.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redstone.common.pojo.EasyUITreeNode;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.content.service.ContentCategoryService;
import com.redstone.mapper.TbContentCategoryMapper;
import com.redstone.pojo.TbContentCategory;
import com.redstone.pojo.TbContentCategoryExample;
import com.redstone.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类管理
 * <p>Title:ContentCategoryServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//根据parentId查询子节点列表
		TbContentCategoryExample example=new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList=new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到结果列表
			resultList.add(node);
		}
		
		return resultList;
	}
	@Override
	public RedStoneResult addContentCategory(Long parentId, String name) {
		//创建一个pojo对象
		TbContentCategory contentCategory=new TbContentCategory();
		//补全对象属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态.可选值：1、正常 2、删除
		contentCategory.setStatus(1);
		//排序，默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()) {
			//如果父节点是叶子节点,应改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return RedStoneResult.ok(contentCategory);
	}
	
	@Override
	public RedStoneResult updateCategory(Long id, String name) {
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		category.setName(name);
		contentCategoryMapper.updateByPrimaryKey(category);
		return RedStoneResult.ok();
	}
	@Override
	public RedStoneResult deleteCategory(Long id) {
		deleteCategoryAndChildNode(id);
		return RedStoneResult.ok();
	}
	/**
	 * 获得所有该节点下的子节点
	 * <p>Title: getChildNodeList</p> 
	 * <p>Description:</p>  
	 * @return List<TbContentCategory>
	 * @param id 父节点id
	 * @return	父节点下所有子节点
	 */
	public List<TbContentCategory> getChildNodeList(Long id){
		//查询所有父节点为id的节点
		TbContentCategoryExample example=new TbContentCategoryExample();
		TbContentCategoryExample.Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		//返回所有符合要求的节点
		return contentCategoryMapper.selectByExample(example);
	}
	/**
	 * 递归删除 1、删除该节点下所有子节点 2、判断删除该节点后父节点是否为叶子节点 3、删除该节点
	 * <p>Title: deleteCategoryAndChildNode</p> 
	 * <p>Description:</p>  
	 * @return void
	 * @param id 要删除的节点id
	 */
	public void deleteCategoryAndChildNode(Long id) {
		//获取要删除的category
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		//判断是否为父节点
		if(category.getIsParent()) {
			//获取所有该节点下的子节点
			List<TbContentCategory> list=getChildNodeList(id);
			//删除所有子节点
			for (TbContentCategory tbContentCategory : list) {
				deleteCategoryAndChildNode(tbContentCategory.getId());
			}
		}
		//判断父节点下是否还有其他子节点
		if(getChildNodeList(category.getParentId()).size()==1) {
			//没有,则将父节点标记为叶子节点
			TbContentCategory parentCategory = contentCategoryMapper.selectByPrimaryKey(category.getParentId());
			parentCategory.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentCategory);
		}
		//删除本节点
		contentCategoryMapper.deleteByPrimaryKey(id);
		return;
	}
}
