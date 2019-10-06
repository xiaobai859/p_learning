package com.pinyougou.pay.service;

import java.util.Map;

public interface WxPayService {


  /**
   * @methodName:createNative
   * @description: 生成支付二维码
   * @author：Xiaobai
   * @createTime：2019年8月15日 下午2:33:16
   * @remarks: @param out_trade_no 商户订单号
   * @remarks: @param total_fee 标价金额
   * @remarks: @return
   * @resultType：Map
   */
  public Map<String, String> createNative(String out_trade_no, String total_fee);


  /**
   * @methodName:queryPayStatus
   * @description: 查询订单支付状态
   * @author：Xiaobai
   * @createTime：2019年8月15日 下午4:15:26
   * @remarks: @param out_trade_no
   * @remarks: @return
   * @resultType：Map<String,String>
   */
  public Map<String, String> queryPayStatus(String out_trade_no);


  /**
   * @methodName:closePay
   * @description: 关闭订单
   * @author：Xiaobai
   * @createTime：2019年8月17日 上午10:41:44
   * @remarks: @param out_trade_no
   * @remarks: @return
   * @resultType：Map<String,String>
   */
  public Map<String, String> closePay(String out_trade_no);


}
