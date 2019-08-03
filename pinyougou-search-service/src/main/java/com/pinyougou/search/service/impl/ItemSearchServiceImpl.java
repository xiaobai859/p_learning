package com.pinyougou.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map search(Map searchMap) {
		Map map = new HashMap<>();
	
		/*Query query = new SimpleQuery("*:*");
		// 添加查询条件
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		*/
		
		// 高亮显示
		HighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); // 设置高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>"); // 设置高亮前缀
		highlightOptions.setSimplePostfix("</em>"); // 设置高亮后缀
		query.setHighlightOptions(highlightOptions); // 设置高亮选项
		
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		SolrDataQuery addCriteria = query.addCriteria(criteria);
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 循环高亮入口集合(每条记录的高亮入口)
		List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
		
		for (HighlightEntry<TbItem> highlightEntry : highlightEntryList) {
			// 获取高亮列表（遍历是取决于设置的高亮域的个数）
			List<Highlight> highlightList = highlightEntry.getHighlights();
			
			/*for (Highlight highlight : highlightList) {
				List<String> snippletList = highlight.getSnipplets();
				System.out.println(snippletList);
			}*/
			if(highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0 ) {
				String highLightString = highlightList.get(0).getSnipplets().get(0); // 获取高亮结果
				TbItem item = highlightEntry.getEntity();
				item.setTitle(highLightString);
			}
		}

		map.put("rows", page.getContent());
		
		return map;
	}

}
