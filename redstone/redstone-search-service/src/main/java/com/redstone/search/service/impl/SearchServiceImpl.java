package com.redstone.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redstone.common.pojo.SearchResult;
import com.redstone.search.dao.SearchDao;
import com.redstone.search.service.SearchService;

/**
 * 搜索服务功能实现
 * <p>Title:SearchServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public SearchResult search(String queryString, int page, int rows)throws Exception {
		//根据查询条件拼装查询对象
		//创建一个SolrQuery对象
		SolrQuery query=new SolrQuery();
		//设置查询条件
		query.setQuery(queryString);
		//设置分页条件
		if(page < 1) { 
			page=1;
		}
		query.setStart((page-1)*rows);
		if(rows<1) {
			rows=10;
		}
		query.setRows(rows);
		//设置默认搜索域
		query.set("df", "item_title");
		//设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<font color='red'>");
		query.setHighlightSimplePost("</font>");
		//调用dao执行查询
		SearchResult result = searchDao.search(query);
		//计算结果总页数
		long recordCount = result.getRecordCount();
		long pages=recordCount / rows;
		if(recordCount % rows >0) {
			pages++;
		}
		result.setTotalPages(pages);
		//返回结果
		return result;
	}

}
