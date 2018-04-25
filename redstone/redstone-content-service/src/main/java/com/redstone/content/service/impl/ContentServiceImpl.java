package com.redstone.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.JsonUtils;
import com.redstone.content.service.ContentService;
import com.redstone.jedis.JedisClient;
import com.redstone.mapper.TbContentMapper;
import com.redstone.pojo.TbContent;
import com.redstone.pojo.TbContentExample;
import com.redstone.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;
	
	@Override
	public RedStoneResult addContent(TbContent content) {
		//补全pojo属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入到内容表
		contentMapper.insert(content);
		//同步缓存
		//删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		return RedStoneResult.ok();
	}

	@Override
	public EasyUIDataGridResult queryContentList(Long categoryId, int page, int rows) {
		//使用插件进行分页
		PageHelper.startPage(page, rows);
		//根据categoryId查询Tbcontent
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		//获取一共有多少页
		PageInfo<TbContent> pageInfo=new PageInfo<>(list);
		//创建EasyUIDataGridResult结果集
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public RedStoneResult updateContent(TbContent content) {
		//补全Pojo
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		content.setUpdated(new Date());
		content.setCreated(tbContent.getCreated());
		contentMapper.updateByPrimaryKeyWithBLOBs(content);
		//同步缓存
		//删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		return RedStoneResult.ok();
	}

	@Override
	public RedStoneResult deleteContent(String ids) {
		String[] list=ids.split(",");
		for (String id : list) {
			//同步缓存
			//删除对应的缓存信息
			TbContent content = contentMapper.selectByPrimaryKey(Long.valueOf(id));
			jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
			//删除内容
			contentMapper.deleteByPrimaryKey(Long.valueOf(id));
		}
		return RedStoneResult.ok();
	}

	@Override
	public List<TbContent> getContentByCid(long cid) {
		//先查询缓存
		//添加缓存不能影响正常的业务逻辑
		try {
			//查询缓存
			String json = jedisClient.hget(INDEX_CONTENT, cid+"");
			//查询到结果,将json转换成List返回
			if(StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有,需要查询数据库
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		//把结果添加到缓存
		try {
			jedisClient.hset(INDEX_CONTENT, cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//返回结果
		return list;
	}

}
