package com.pinyougou.content.service.impl;
import java.util.List;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		// 清除缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		/**
		 *   删除缓存时要注意，是否修改了广告的分类id,如果分类id也进行修改了，则分类前后的缓存都要进行清除
		 *   主要先获取广告的主键id（分类id不管如何改变，主键id都不会变化）
		 */
		Long id = content.getId(); // 获取主键id
		// 修改前：通过主键id查询数据库中的分类id，清除缓存
		Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId(); 
		redisTemplate.boundHashOps("content").delete(categoryId); // 修改前清除缓存
		
		contentMapper.updateByPrimaryKey(content);
		// 修改后，判断分类id是否发生改变，改变则清除新的分类id缓存，未改变则无需进行操作
		if(content.getCategoryId() != categoryId) {
			System.out.println("分类id改变，清除新id缓存");
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		
		
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			// 清除缓存，先通过主键id查询当前广告分类id,然后再进行删除
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据分类id查询广告列表	
	 */
	@Autowired
	private RedisTemplate redisTemplate;
		
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		
		
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		
		if(list == null) { // 缓存中没有数据，从数据库进行查询
			System.out.println("到数据库中进行查询并放入缓存");
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("sort_order"); // 排序
			list = contentMapper.selectByExample(example);
			// 查询到数据库后，将数据放到缓存中
			redisTemplate.boundHashOps("content").put(categoryId, list);
		} else {
			System.out.println("从缓存中查询数据");	
		}
		return list;

	}
	
}
