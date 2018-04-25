package com.redstone.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.IDUtils;
import com.redstone.common.utils.JsonUtils;
import com.redstone.jedis.JedisClient;
import com.redstone.mapper.TbItemDescMapper;
import com.redstone.mapper.TbItemMapper;
import com.redstone.pojo.TbContent;
import com.redstone.pojo.TbItem;
import com.redstone.pojo.TbItemDesc;
import com.redstone.pojo.TbItemExample;
import com.redstone.service.ItemService;
/**
 * 商品管理service
 * <p>Title:ItemServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @date 2017年11月28日
 * @version V1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name="itemAddTopic")
	private Destination destination;
	@Resource(name="itemDeleteTopic")
	private Destination destination_delete;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	@Value("${ITEM_EXPIRE}")
	private Integer ITEM_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		//查询数据库前先查询缓存
		try {
			String json = jedisClient.get(ITEM_INFO+":"+itemId+":BASE");
			if(StringUtils.isNotBlank(json)) {
				//把JSON数据转换成pojo
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有再查询数据库
		TbItem item=itemMapper.selectByPrimaryKey(itemId);
		try {
			//把查询结果添加到缓存
			jedisClient.set(ITEM_INFO+":"+itemId+":BASE", JsonUtils.objectToJson(item));
			//设置过期时间,提高缓存的利用率
			jedisClient.expire(ITEM_INFO+":"+itemId+":BASE",ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example=new TbItemExample();
		List<TbItem> list=itemMapper.selectByExample(example);
		//取得查询结果
		PageInfo<TbItem> pageInfo=new PageInfo<>(list);
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public RedStoneResult addItem(TbItem item, String desc) {
		//生成商品ID
		final long itemId = IDUtils.genItemId();
		//补全item属性
		item.setId(itemId);
		//商品状态,1-正常,2-下架,3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//向商品表中插入数据
		itemMapper.insert(item);
		//创建一个商品描述表对应的pojo
		TbItemDesc itemDesc=new TbItemDesc();
		//补全pojo的属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//向ActiveMq发送商品添加消息
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				//发送商品id
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		//返回结果
		return RedStoneResult.ok();
	}

	@Override
	public RedStoneResult getItemDescById(long itemId) {
		
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		RedStoneResult result = RedStoneResult.ok(tbItemDesc);
		return result;
	}

	
	@Override
	public TbItemDesc getItemDesc(long itemId) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		return itemDesc;
	}

	@Override
	public RedStoneResult deleteItem(String ids) {
		String[] list=ids.split(",");
		for (String id : list) {
			itemMapper.deleteByPrimaryKey(Long.valueOf(id));
			jmsTemplate.send(destination_delete, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					//发送商品id
					TextMessage textMessage = session.createTextMessage(id + "");
					return textMessage;
				}
			});
		}
		return RedStoneResult.ok();
	}

	/**
	 * 下架商品
	 * <p>Title: instockItem</p> 
	 * <p>Description:</p>  
	 * @param ids
	 * @return
	 */
	@Override
	public RedStoneResult instockItem(String ids) {
		String[] list=ids.split(",");
		for (String id : list) {
			TbItem tbItem = itemMapper.selectByPrimaryKey(Long.valueOf(id));
			tbItem.setStatus((byte) 2);
			tbItem.setUpdated(new Date());
			itemMapper.updateByPrimaryKey(tbItem);
		}
		return RedStoneResult.ok();
	}
	/**
	 * 上架商品
	 * <p>Title: reshelfItem</p> 
	 * <p>Description:</p>  
	 * @param ids
	 * @return
	 */
	@Override
	public RedStoneResult reshelfItem(String ids) {
		String[] list=ids.split(",");
		for (String id : list) {
			TbItem tbItem = itemMapper.selectByPrimaryKey(Long.valueOf(id));
			tbItem.setStatus((byte) 1);
			tbItem.setUpdated(new Date());
			itemMapper.updateByPrimaryKey(tbItem);
		}
		return RedStoneResult.ok();
	}

	/**
	 * 修改商品
	 * <p>Title: updateItem</p> 
	 * <p>Description:</p>  
	 * @param item
	 * @return
	 */
	@Override
	public RedStoneResult updateItem(TbItem item,String desc) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(item.getId());
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(tbItem.getId());
		tbItem.setCid(item.getCid());
		tbItem.setImage(item.getImage());
		tbItem.setNum(item.getNum());
		tbItem.setPrice(item.getPrice());
		tbItem.setSellPoint(item.getSellPoint());
		tbItem.setBarcode(item.getBarcode());
		tbItem.setTitle(item.getTitle());
		tbItem.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(tbItem);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setUpdated(new Date());
		itemDescMapper.updateByPrimaryKeyWithBLOBs(tbItemDesc);
		
		return RedStoneResult.ok();
	}


	
}
