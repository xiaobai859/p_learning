package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Reference
	private CartService cartService;
	
	/**
	 * @methodusername:findCartList
	 * @description: 查找购物车列表
	 * @author：Xiaobai
	 * @createTime：2019年8月11日 下午5:45:18
	 * @remarks: @return
	 * @resultType：List<Cart>
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
		if(cartListString==null || cartListString.equals("")){
			cartListString="[]";
		}
		List<Cart> cartListFromCookie = JSON.parseArray(cartListString, Cart.class);
		if("anonymousUser".equals(username)) { // 未登录
			System.out.println("从cookie中提取购物车");
			return cartListFromCookie;
			
		} else { // 已登录 判断cookie中是否有购物车，有则与redis中购物车进行合并
			List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username); // 获取redis中购物车
			
			if(cartListFromCookie.size() > 0) { // cookie中有购物车进行合并
				List<Cart> cartList = cartService.mergeCartList(cartListFromCookie, cartListFromRedis);
				// 合并后，清除cookie中购物车
				CookieUtil.deleteCookie(request, response, "cartList");
				System.out.println("合并购物车");
				// 将合并后的购物车存入redis中
				cartService.saveCartListToRedis(username, cartList);
			}
			return cartListFromRedis;
			
		}
		
		
	}
	
	
	/**
	 * @methodusername:addGoodsToCartList
	 * @description: 添加商品到购物车
	 * @author：Xiaobai
	 * @createTime：2019年8月11日 下午5:45:00
	 * @remarks: @param item
	 * @remarks: @param num
	 * @remarks: @return
	 * @resultType：Result
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		try {
			// 1.从cookie中取出购物车
			List<Cart> cartList = findCartList();
			// 2.向购物车添加商品
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if("anonymousUser".equals(username)) { // 未登录
				String cartListString = JSON.toJSONString(cartList);
				// 3.将购物车存入cookie 
				CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "utf-8");
				System.out.println("向cookie中存储购物车");
				
			} else { // 已登录
				cartService.saveCartListToRedis(username, cartList);
				System.out.println("向redis中存储购物车");
			}
			return new Result(true, "商品添加购物车成功");
			
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "商品添加购物车失败");
			
		}

	}
	
	

}
