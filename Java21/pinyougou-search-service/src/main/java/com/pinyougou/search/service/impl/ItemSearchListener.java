package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Component
public class ItemSearchListener implements MessageListener {

  @Autowired
  private ItemSearchService itemSearchService;

  @Override
  public void onMessage(Message message) {
    TextMessage textMessage = (TextMessage) message;
    System.out.println("监听收到消息");
    try {
      String text = textMessage.getText(); // JSON字符串
      List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
      itemSearchService.importList(itemList);
      System.out.println("成功导入到solr索引库");
    } catch (JMSException e) {
      e.printStackTrace();
    }

  }

}
