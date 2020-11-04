package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbOrderItem;

/**
 * @typeName: Cart
 * @classDesc: 购物车对象
 * @author：XiaoBai
 * @createTime：2019年8月11日 下午2:48:26
 * @remarks： 
 * @version V1.0
 */
public class Cart implements Serializable {
	
	private String sellerId;  // 商家id
	private String sellerName; // 商家名称
	private List<TbOrderItem> orderItemList; // 购物车商品明细对象
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public List<TbOrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<TbOrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	
	
	

}
