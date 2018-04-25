package com.redstone.content.service;

import java.util.List;

import com.redstone.common.pojo.EasyUITreeNode;
import com.redstone.common.pojo.RedStoneResult;

public interface ContentCategoryService {

	List<EasyUITreeNode> getContentCategoryList(long parentId);
	RedStoneResult addContentCategory(Long parentId,String name);
	RedStoneResult updateCategory(Long id,String name);
	RedStoneResult deleteCategory(Long id);
}
