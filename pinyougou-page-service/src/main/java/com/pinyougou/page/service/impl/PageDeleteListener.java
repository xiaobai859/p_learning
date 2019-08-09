package com.pinyougou.page.service.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageDeleteListener implements MessageListener {
	
	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		
		ObjectMessage objectMessage = (ObjectMessage) message;
		System.out.println("监听收到消息");
		try {
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			for (Long goodsId : goodsIds) {
				boolean b = itemPageService.deleteItemHtml(goodsId);
				System.out.println("商品详情页删除结果：" + goodsId + b);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
