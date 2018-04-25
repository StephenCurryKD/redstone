package com.redstone.search.service;

import com.redstone.common.pojo.SearchResult;

public interface SearchService {

	SearchResult search(String queryString,int page,int rows)throws Exception;
}
