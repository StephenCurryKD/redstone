package com.redstone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.search.service.SearchItemService;

/**
 * 索引库维护Controller
 * <p>Title:IndexManagerController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class IndexManagerController {

	@Autowired
	private SearchItemService searchItemService;
	
	@RequestMapping("/index/import")
	@ResponseBody
	public RedStoneResult importIndex() {
		RedStoneResult result = searchItemService.importItemsToIndex();
		return result;
	}
}
