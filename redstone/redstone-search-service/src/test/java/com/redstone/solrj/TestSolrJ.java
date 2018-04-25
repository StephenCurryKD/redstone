package com.redstone.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	// 添加索引
	@Test
	public void testAddDocument()throws Exception{
		//创建一个SolrServer对象，创建一个HttpSolrServer对象
		//需要指定solr服务的url
		SolrServer solrServer=new HttpSolrServer("http://47.100.114.233:8080/solr/collection1");
		//创建一个文档对象SolrInputDocument
		SolrInputDocument document=new SolrInputDocument();		
		//向文档中添加域,必须有id域,域的名称必须在schema.xml中定义
		document.addField("id", "test02");
		document.addField("item_title", "测试商品2");
		document.addField("item_price", 1000);
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	
	//删除索引
	@Test
	public void deleteDocumentById()throws Exception{
		
		SolrServer solrServer=new HttpSolrServer("http://47.100.114.233:8080/solr/collection1");
		solrServer.deleteById("test02");
		solrServer.commit();
	}
	
	//删除索引根据条件
	@Test
	public void deleteDocumentByQuery()throws Exception{
		
		SolrServer solrServer=new HttpSolrServer("http://47.100.114.233:8080/solr/collection1");
		//solrServer.deleteByQuery("*:*");//删除所有
		//solrServer.deleteByQuery("item_title:123");
		solrServer.commit();
	}
	
	//查询索引
	@Test
	public void queryDocument()throws Exception{
		//创建一个SolrService对象
		SolrServer solrServer=new HttpSolrServer("http://47.100.114.233:8080/solr/collection1");
		//创建一个SolrQuery对象
		SolrQuery query=new SolrQuery();
		//设置查询条件、过滤条件、分页条件、排序条件、高亮
		//query.set("q", "*:*");  
		query.setQuery("手机");  //等价的
		//分页条件
		query.setStart(0); 
		query.setRows(10);
		//设置默认搜索域
		query.set("df", "item_keywords");
		//设置高亮
		query.setHighlight(true); //开启高亮
		query.addHighlightField("item_title");//高亮显示的域
		query.setHighlightSimplePre("<em>");//高亮前缀
		query.setHighlightSimplePost("</em>");//高亮后缀
		//执行查询,得到一个Response对象
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList results = response.getResults();
		//取查询结果总记录数
		System.out.println("查询结果总记录数:"+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println("商品编号:"+solrDocument.get("id"));
			//取高亮显示
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String item_title="";
			if(list !=null && list.size()>0) {
				item_title=list.get(0);
			}else {
				item_title=(String) solrDocument.get("item_title");
			}
			System.out.println("标题:"+item_title);
			System.out.println("卖点:"+solrDocument.get("item_sell_point"));
			System.out.println("价格:"+solrDocument.get("item_price"));
			System.out.println("图片:"+solrDocument.get("item_image"));
			System.out.println("分类名称:"+solrDocument.get("item_category_name"));
			System.out.println("==================================================");
		}
		//取查询结果
	}
	
}
