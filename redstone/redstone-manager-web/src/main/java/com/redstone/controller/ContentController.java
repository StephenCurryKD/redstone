package com.redstone.controller;
/**
 * 内容管理Controller
 * <p>Title:ContentController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.content.service.ContentService;
import com.redstone.pojo.TbContent;

@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/save")
	@ResponseBody
	public RedStoneResult addContent(TbContent content) {
		RedStoneResult result = contentService.addContent(content);
		return result;
	}
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult queryContentList(Long categoryId, int page, int rows) {
		return contentService.queryContentList(categoryId, page, rows);
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public RedStoneResult updateContent(TbContent content) {
		RedStoneResult result = contentService.updateContent(content);
		return result;
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public RedStoneResult deleteContent(String ids) {
		RedStoneResult result = contentService.deleteContent(ids);
		return result;
	}
	
}
