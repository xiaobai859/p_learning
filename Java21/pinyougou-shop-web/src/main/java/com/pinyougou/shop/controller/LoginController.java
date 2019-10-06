package com.pinyougou.shop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version V1.0
 * @typeName: LoginController
 * @classDesc: 显示登陆的用户名
 * @author：XiaoBai
 * @createTime：2019年7月24日 下午4:02:17
 * @remarks：
 */
@RestController
@RequestMapping("/login")
public class LoginController {


  @RequestMapping("/showName")
  public Map showName() {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    Map map = new HashMap();
    map.put("loginName", name);
    return map;
  }

}
