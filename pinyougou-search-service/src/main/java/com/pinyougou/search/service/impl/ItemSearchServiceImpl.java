package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

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
		// 去除搜索关键字中存在空格
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		// 查询列表
		map.putAll(searchList(searchMap));
		// 分组查询
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		// 查询品牌和规格列表
		String category = (String) searchMap.get("category");
		if(!"".equals(category)) { // 用户选择了商品分类
			map.putAll(searchBrandAndSpecList(category));
		} else {
			if(categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));		
			}
		}
		
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
		query.addCriteria(criteria);
	// 在查询高亮显示前添加搜索项
		
		// 按照商品分类过滤
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery );
		}
		
		// 按照品牌过滤
		if(!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery );
		}
		
		// 按照规格过滤
		if(searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for( String key : specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
		}
		
		// 按照价格区间过滤
		if(!"".equals(searchMap.get("price"))) { 
			String priceStr = (String) searchMap.get("price");
			String[] price = priceStr.split("-");
			if(!"0".equals(price[0])) { // 最低价格不为0
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
			if(!"*".equals(price[1])) { // 最高价格不为*
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
		}
		
		// 分页
		Integer pageNum =  (Integer) searchMap.get("pageNum"); // 页码
		if(pageNum == null) {
			pageNum = 1;
		}
		
		Integer pageSize = (Integer) searchMap.get("pageSize"); // 每页显示的条数
		if (pageSize == null) {
			pageSize = 20;
		}
		
		query.setOffset((pageNum - 1) * pageSize); // 设置初始查询数
		query.setRows(pageSize); // 设置每页显示条数
		
		
		// 排序
		String sortValue = (String) searchMap.get("sort"); // 排序方式：升序ASC 降序DESC
		String sortField = (String) searchMap.get("sortField"); // 需要排序的字段
		System.out.println(sortField);
		
		if(sortValue != null && !sortValue.equals("")) { // 根据排序方式进行排序
			if("ASC".equals(sortValue)) { // 采用升序方式
				Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
				query.addSort(sort);
			}
			if("DESC".equals(sortValue)) { // 采用降序方式
				Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
				query.addSort(sort);
			}
		}
		
		
		
		// 高亮显示处理
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
		map.put("totalPages", page.getTotalPages()); // 返回总页数
		map.put("total", page.getTotalElements()); // 返回总记录数

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
		query.addCriteria(criteria);
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
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * @methodName:searchBrandAndSpecList
	 * @description: 根据商品分类从缓存中查询品牌和规格列表
	 * @author：Xiaobai
	 * @createTime：2019年8月3日 下午6:54:33
	 * @remarks: @return
	 * @resultType：Map
	 */
	public Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		// 根据商品分类获得模板id
		Long typeTemplateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		
		if(typeTemplateId != null) {
			// 根据模板id查询品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
			map.put("brandList", brandList);
			// 根据模板id查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeTemplateId);
			map.put("specList", specList);
		}
		
		return map;
	}


	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
	}


	@Override
	public void deleteByGoodsIds(List list) {
		
		System.out.println("删除商品ID"+list);
		SolrDataQuery query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsids").in(list);
		query.addCriteria(criteria );
		solrTemplate.delete(query );
		solrTemplate.commit();
		
	}
}
