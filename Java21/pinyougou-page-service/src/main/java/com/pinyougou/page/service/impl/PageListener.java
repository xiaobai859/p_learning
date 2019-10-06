package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

/**
 * @version V1.0
 * @typeName: PageListener
 * @classDesc: 用于生成商品详情页监听类
 * @author：XiaoBai
 * @createTime：2019年8月8日 下午4:43:34
 * @remarks：
 */
@Component
public class PageListener implements MessageListener {

  @Autowired
  private ItemPageService itemPageService;

  @Override
  public void onMessage(Message message) {
    System.out.println("监听接收到消息");
    TextMessage textMessage = (TextMessage) message;
    try {
      String text = textMessage.getText();
      boolean b = itemPageService.genItemHtml(Long.parseLong(text));
      System.out.println("网页生成结果：" + b);
    } catch (JMSException e) {
      e.printStackTrace();
    }

  }

}
