package com.redstone.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.redstone.common.pojo.SearchItem;
import com.redstone.search.mapper.SearchItemMapper;

/**
 * 监听商品删除事件,同步索引库
 * <p>Title:ItemAddMessageListener</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
public class ItemDeleteMessageListener implements MessageListener{

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		try {
			//从消息中取得商品id
			TextMessage textMessage=(TextMessage) message;
			String text = textMessage.getText();
			long itemId=Long.parseLong(text);
			//根据商品id查询数据，取得商品信息
			//等待事务提交
			Thread.sleep(1000);
			solrServer.deleteById(String.valueOf(itemId));
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
						
	}
	
}
