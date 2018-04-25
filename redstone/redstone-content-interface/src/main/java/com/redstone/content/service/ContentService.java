package com.redstone.content.service;

import java.util.List;

import com.redstone.common.pojo.EasyUIDataGridResult;
import com.redstone.common.pojo.RedStoneResult;
import com.redstone.pojo.TbContent;

public interface ContentService {

	EasyUIDataGridResult queryContentList(Long categoryId,int page,int rows);
	RedStoneResult addContent(TbContent content);
	RedStoneResult updateContent(TbContent content);
	RedStoneResult deleteContent(String ids);
	List<TbContent> getContentByCid(long cid);
}
