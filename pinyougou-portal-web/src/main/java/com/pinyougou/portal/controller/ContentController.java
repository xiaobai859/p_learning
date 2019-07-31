package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;
	
	/**
	 * @methodName:findByCategoryId
	 * @description: 根据广告分类查询广告列表
	 * @author：Xiaobai
	 * @createTime：2019年7月31日 下午4:45:15
	 * @remarks: @param id
	 * @remarks: @return
	 * @resultType：List<TbContent>
	 */
	@RequestMapping("/findByCategroyId")
	public List<TbContent> findByCategoryId(Long categoryId) {
		return contentService.findByCategoryId(categoryId);
	}

}
