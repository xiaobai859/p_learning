package com.pinyougou.cart.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WxPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

  @Reference
  private WxPayService wxPayService;

  @Reference
  private OrderService orderService;

  @RequestMapping("/createNative")
  public Map<String, String> createNative() {
    //获取当前登录用户
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    System.out.println("当前登录的用户名" + username);
    // 从redis中查询支付日志
    TbPayLog payLog = orderService.searchPayLogFromRedis(username);
    System.out.println(payLog);
    if (payLog != null) { // 支付日志是否存在
      return wxPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");

    } else {
      return new HashMap<String, String>();
    }
  }


  @RequestMapping("/queryPayStatus")
  public Result queryPayStatus(String out_trade_no) {
    Result result = null;
    int i = 0;
    while (true) {
      Map<String, String> map = wxPayService.queryPayStatus(out_trade_no);
      if (map == null) {
        result = new Result(false, "支付错误");
        break;
      }
      if ("SUCCESS".equals(map.get("trade_state"))) { // 支付成功
        String transaction_id = map.get("transaction_id"); // 微信返回的订单流水号
        orderService.updateOrderStatus(out_trade_no, transaction_id); // 修改订单状态
        result = new Result(true, "支付成功");
        break;
      }

      try {
        Thread.sleep(3000); // 设置每次隔3秒查询一次
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (i++ > 100) {
        // 五分钟后还未支付关闭订单
        result = new Result(false, "二维码超时");
        break;
      }
    }
    return result;
  }


}
