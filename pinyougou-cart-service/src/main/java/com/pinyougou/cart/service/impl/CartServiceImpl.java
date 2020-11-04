package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private TbItemMapper orderMapper;

	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		// 1.根据skuID查询商品明细SKU对象
		TbItem item = orderMapper.selectByPrimaryKey(itemId);
		if(item == null) {
			throw new RuntimeException("商品不存在");
		}
		
		if(!"1".equals(item.getStatus())) {
			throw new RuntimeException("商品状态无效");
		}
		// 2.根据SKU对象得到商家ID
		String sellerId = item.getSellerId();
		
		// 3.根据商家ID在购物车列表中查询购物车对象
		Cart cart = findCartBySellerId(cartList, sellerId); // 调用方法返回cart,cart为空则不存在
		
		if(cart == null) { // 4.如果购物车列表中不存在该商家购物车
			System.out.println("购物车中不存在该商家购物车");
			// 4.1.创建一个新的购物车对象   cart
			cart = new Cart();
			TbOrderItem orderItem = createOrderItem(item, num); // 创建订单对象
			List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
			orderItemList.add(orderItem);
			// 4.2.将购物车对象添加到购物车列表中
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			cart.setOrderItemList(orderItemList);
			cartList.add(cart);
			
		} else { // 5.如果购物车中已存在该商家购物车
			// 判断该商品是否在购物车明细列表中
			System.out.println("购物车中已存在该商家购物车");
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			TbOrderItem orderItem = findItemByItemId(orderItemList, itemId);
			System.out.println("goodsId: " + item.getGoodsId());
			System.out.println("itemId: " + itemId);
			if(orderItem == null) { // 5.1.不存在，创建新的购物车明细对象 orderItem，并添加到购物车明细列表中   
				orderItem = createOrderItem(item, num); // 新的购物车明细对象
				cart.getOrderItemList().add(orderItem); // 将购物车明细对象添加到购物车明细列表中
				
			}else { // 5.2.存在,在原有的明细列表中添加数量，并跟新金额
				System.out.println("购物车中已存在该商品");
				orderItem.setNum(orderItem.getNum() + num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
				// 如果购物车中商品数量小于等于0，则从购物车中移除
				if(orderItem.getNum() <= 0) {
					cart.getOrderItemList().remove(orderItem); // 移除此条购物车明细对象
				}
				
				// 如果移除后cart的明细数量为0，则将cart移除
				if(cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
			
		}
		return cartList;
	}
	
	
	/**
	 * @methodName:findCartBySellerId
	 * @description: 根据商家ID查询购物车对象
	 * @author：Xiaobai
	 * @createTime：2019年8月11日 下午3:29:47
	 * @remarks: @param cartList
	 * @remarks: @param sellerId
	 * @remarks: @return
	 * @resultType：Cart
	 */
	public Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
		
		for (Cart cart : cartList) {
			if(sellerId.equals(cart.getSellerId())) {
				return cart;
			}
		}
		return null;
	}
	

	/**
	 * @methodName:createOrderItem
	 * @description: 创建订单明细对象
	 * @author：Xiaobai
	 * @createTime：2019年8月11日 下午3:54:08
	 * @remarks: @param item
	 * @remarks: @param num
	 * @remarks: @return
	 * @resultType：TbOrderItem
	 */
	public TbOrderItem createOrderItem(TbItem item, Integer num) {
		if(num <= 0) {
			throw new RuntimeException("数据非法");
		}
		// 创建购物车明细对象
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	
	
	/**
	 * @methodName:findItemBygoodsId
	 * @description: 根据商品id查询购物明细表
	 * @author：Xiaobai
	 * @createTime：2019年8月11日 下午4:09:28
	 * @remarks: @param orderItemList
	 * @remarks: @param goodsId
	 * @remarks: @return
	 * @resultType：TbOrderItem
	 */
	public TbOrderItem findItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		for (TbOrderItem orderItem : orderItemList) {
			if(orderItem.getItemId().longValue() == itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中读取购物车 " + username);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList == null) {
			cartList = new ArrayList();
		}
		return cartList;
	}


	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis中存放购物车");
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}


	// 合并购物车
	@Override
	public List<Cart> mergeCartList(List<Cart> cartListFromCookie, List<Cart> cartListFromRedis) {
		for (Cart cart : cartListFromCookie) { // 遍历cookie中购物车，将其添加到redis中
			for(TbOrderItem orderItem : cart.getOrderItemList()) {
				cartListFromRedis = addGoodsToCartList(cartListFromRedis, orderItem.getItemId(), orderItem.getNum());
			}
		}
		// 清空购物车
		return cartListFromRedis;
	}
	
	
	
	
	
	

}
