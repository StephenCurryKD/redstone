package com.redstone.search.mapper;

import java.util.List;

import com.redstone.common.pojo.SearchItem;

public interface SearchItemMapper {

	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
