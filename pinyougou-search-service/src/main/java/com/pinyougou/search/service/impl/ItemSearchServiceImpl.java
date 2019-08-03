package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Crotch;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
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

		// 查询列表
		map.putAll(searchList(searchMap));
		// 分组查询
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		return map;
	}
	
	
	/**
	 * @methodName:searchList
	 * @description: 查询列表
	 * @author：Xiaobai
	 * @createTime：2019年8月3日 下午3:18:30
	 * @remarks: @param searchMap
	 * @remarks: @return
	 * @resultType：Map
	 */
	public Map searchList(Map searchMap) {
		Map map = new HashMap<>();
		
		/*Query query = new SimpleQuery("*:*");
		// 添加查询条件
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		*/
		
		// 高亮显示
		HighlightQuery query = new SimpleHighlightQuery();
		// 设置高亮域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); 
		// 设置高亮前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>"); 
		// 设置高亮后缀
		highlightOptions.setSimplePostfix("</em>"); 
		// 设置高亮选项
		query.setHighlightOptions(highlightOptions); 
		// 添加查询条件
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

	/**
	 * @methodName:searchCategoryList
	 * @description: 分组查询，商品分类列表
	 * @author：Xiaobai
	 * @createTime：2019年8月3日 下午3:20:02
	 * @remarks: @param searchMap
	 * @remarks: @return
	 * @resultType：List<String>
	 */
	public List<String> searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<String>();
		
		Query query = new SimpleQuery("*:*");
		// 根据关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		SolrDataQuery solrDataQuery = query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 获取分组页
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query , TbItem.class);
		// 获取分组结果对象
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		// 获取分组入口也
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 获取分组入口集合
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : entryList) {
			String groupValue = entry.getGroupValue(); // 获取分组结果
			list.add(groupValue); // 将分组结果添加到返回值
		}
		
		return list;
	}
}
