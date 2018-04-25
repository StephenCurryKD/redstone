package com.redstone.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.SearchItem;
import com.redstone.common.pojo.SearchResult;
import com.redstone.search.service.SearchService;

/**
 * 搜索服务Controller
 * <p>Title:SearchController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;
	
	@RequestMapping("/search")
	public String search(@RequestParam("q")String queryString,
			@RequestParam(defaultValue="1")Integer page,Model model)throws Exception {
		    
			//调用服务执行查询
			//把查询条件进行转码,解决get乱码问题
			queryString=new String(queryString.getBytes("iso8859-1"),"utf-8");
			SearchResult result = searchService.search(queryString, page, SEARCH_RESULT_ROWS);
			//把结果传递给页面
			model.addAttribute("query", queryString);
			model.addAttribute("totalPages", result.getTotalPages());
			model.addAttribute("itemList", result.getItemList());
			model.addAttribute("page", page);
		//返回逻辑视图
		return "search";
	}
}
