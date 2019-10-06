package com.pinyougou.seckill.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WxPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

  @Reference
  private WxPayService wxPayService;

  @Reference
  private SeckillOrderService seckillOrderService;

  @RequestMapping("/createNative")
  public Map<String, String> createNative() {
    //获取当前登录用户
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    System.out.println("当前登录的用户名" + username);
    // 从redis中查询支付日志
    TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedis(username);
    System.out.println(seckillOrder);
    if (seckillOrder != null) { // 支付日志是否存在
      return wxPayService.createNative(seckillOrder.getId() + "",
          (long) (seckillOrder.getMoney().doubleValue() * 100) + "");

    } else {
      return new HashMap<String, String>();
    }
  }


  @RequestMapping("/queryPayStatus")
  public Result queryPayStatus(String out_trade_no) {
    //获取当前登录用户
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

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
        seckillOrderService
            .saveOrderFromRedisToDB(username, Long.valueOf(out_trade_no), transaction_id); // 保存订单
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
        // 关闭支付
        Map<String, String> payResultMap = wxPayService.closePay(out_trade_no);
        if (payResultMap != null && "FAIL".equals(payResultMap.get("return_code"))) {
          if ("ORDERPAID".equals(payResultMap.get("err_code"))) { // 已经支付了，正常保存订单
            String transaction_id = map.get("transaction_id"); // 微信返回的订单流水号
            seckillOrderService.saveOrderFromRedisToDB(username, Long.valueOf(out_trade_no),
                transaction_id); // 保存订单
          }
        }

        // 删除订单
        if (result.isSuccess() == false) {
          seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(out_trade_no));
        }

        break;
      }
    }
    return result;
  }


}
