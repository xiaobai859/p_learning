package com.pinyougou.page.service;

public interface ItemPageService {

	/**
	 * @methodName:genItemHtml
	 * @description: 生成商品详情页
	 * @author：Xiaobai
	 * @createTime：2019年8月6日 上午11:16:43
	 * @remarks: @param goodsId
	 * @remarks: @return
	 * @resultType：boolean
	 */
	public boolean genItemHtml(Long goodsId);
	
	/**
	 * @methodName:deleteItemHtml
	 * @description: 删除商品详情页
	 * @author：Xiaobai
	 * @createTime：2019年8月8日 下午5:10:40
	 * @remarks: @param goodId
	 * @remarks: @return
	 * @resultType：boolean
	 */
	public boolean deleteItemHtml(Long goodsId);
}
