package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

	@Reference
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/search")
	public Map search(Map searchMap) {
		
		return itemSearchService.search(searchMap);
		
	}
	
}
