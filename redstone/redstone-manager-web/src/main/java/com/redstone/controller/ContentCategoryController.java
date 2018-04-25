package com.redstone.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.EasyUITreeNode;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.content.service.ContentCategoryService;

/**
 * 内容管理Controller
 * <p>Title:ContentCategoryController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value="id",defaultValue="0")Long parentId){
		List<EasyUITreeNode> list = contentCategoryService.getContentCategoryList(parentId);
		return list;
	}
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public RedStoneResult addContentCategory(Long parentId,String name) {
		RedStoneResult result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	@RequestMapping("/content/category/update")
	@ResponseBody
	public RedStoneResult updateCategory(Long id,String name) {
		RedStoneResult result = contentCategoryService.updateCategory(id, name);
		return result;
	}
	
	@RequestMapping("/content/category/delete")
	@ResponseBody
	public RedStoneResult deleteCategory(Long id) {
		RedStoneResult result = contentCategoryService.deleteCategory(id);
		return result;
	}
	
}
