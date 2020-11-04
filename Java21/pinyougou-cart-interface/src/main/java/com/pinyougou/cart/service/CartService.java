package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * @version V1.0
 * @typeName: CartService
 * @classDesc: 购物车接口
 * @author：XiaoBai
 * @createTime：2019年8月11日 下午2:56:28
 * @remarks：
 */
public interface CartService {

  /**
   * @methodName:addGoodsToCartList
   * @description: 添加商品到购物车
   * @author：Xiaobai
   * @createTime：2019年8月11日 下午2:56:47
   * @remarks: @param cartList 购物车列表
   * @remarks: @param itemId 商品SKUID
   * @remarks: @param num 数量
   * @remarks: @return
   * @resultType：List<Cart>
   */
  public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);


  /**
   * @methodName:findCartListFromRedis
   * @description: 从redis中查询购物车
   * @author：Xiaobai
   * @createTime：2019年8月12日 下午2:33:55
   * @remarks: @param username
   * @remarks: @return
   * @resultType：List<Cart>
   */
  public List<Cart> findCartListFromRedis(String username);


  /**
   * @methodName:saveCartListToRedis
   * @description: 将购物车添加到redis缓存
   * @author：Xiaobai
   * @createTime：2019年8月12日 下午2:35:22
   * @remarks: @param username
   * @remarks: @param cartList
   * @resultType：void
   */
  public void saveCartListToRedis(String username, List<Cart> cartList);

  /**
   * @methodName:mergeCartList
   * @description: 合并cookie和redis购物车列表，将cookie购物车存入到redis中
   * @author：Xiaobai
   * @createTime：2019年8月12日 下午3:33:04
   * @remarks: @param cartListFromCookie
   * @remarks: @param cartListFromRedis
   * @remarks: @return
   * @resultType：List<Cart>
   */
  public List<Cart> mergeCartList(List<Cart> cartListFromCookie, List<Cart> cartListFromRedis);


}
