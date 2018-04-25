package com.redstone.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.pojo.SearchItem;
import com.redstone.search.mapper.SearchItemMapper;
import com.redstone.search.service.SearchItemService;

/**
 * 索引库导入solr
 * <p>Title:SearchItemServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public RedStoneResult importItemsToIndex() {
		try {
		//1、先查询所有商品数据
		List<SearchItem> list = searchItemMapper.getItemList();
		//2、遍历商品数据添加到索引库
		for (SearchItem searchItem : list) {
			//创建文档对象
			SolrInputDocument document=new SolrInputDocument();
			//向文档中添加域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			//document.addField("item_desc", searchItem.getItem_desc());
			//把文档写入索引库
			solrServer.add(document);
		}
		//3、提交
		solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return RedStoneResult.build(500, "数据导入失败");
		}
		//4、返回添加成功
		return RedStoneResult.ok();
	}

}
