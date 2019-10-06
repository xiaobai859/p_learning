package com.pinyougou.manager.controller;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

  @Reference
  private GoodsService goodsService;

  /**
   * 返回全部列表
   *
   * @return
   */
  @RequestMapping("/findAll")
  public List<TbGoods> findAll() {
    return goodsService.findAll();
  }


  /**
   * 返回全部列表
   *
   * @return
   */
  @RequestMapping("/findPage")
  public PageResult findPage(int page, int rows) {
    return goodsService.findPage(page, rows);
  }

  /**
   * 增加
   * @param goods
   * @return
   */
/*	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(sellerId);
		
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}*/

  /**
   * 修改
   *
   * @param goods
   * @return
   */
  @RequestMapping("/update")
  public Result update(@RequestBody Goods goods) {
    try {
      goodsService.update(goods);
      return new Result(true, "修改成功");
    } catch (Exception e) {
      e.printStackTrace();
      return new Result(false, "修改失败");
    }
  }

  /**
   * 获取实体
   *
   * @param id
   * @return
   */
  @RequestMapping("/findOne")
  public Goods findOne(Long id) {
    return goodsService.findOne(id);
  }

  @Autowired
  private Destination queueSolrDeleteDestination; // 用于将商品信息从索引库中删除

  @Autowired
  private Destination topicPageDeleteDestination; // 用于删除生成的商品详情页

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @RequestMapping("/delete")
  public Result delete(Long[] ids) {
    try {
      goodsService.delete(ids);

      // itemSearchService.deleteByGoodsIds(Arrays.asList(ids)); // 从索引库中删除
      jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {

        @Override
        public Message createMessage(Session session) throws JMSException {
          return session.createObjectMessage(ids);
        }
      });

      // 删除商品详情页
      jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {

        @Override
        public Message createMessage(Session session) throws JMSException {

          return session.createObjectMessage(ids);
        }
      });

      return new Result(true, "删除成功");
    } catch (Exception e) {
      e.printStackTrace();
      return new Result(false, "删除失败");
    }
  }

  /**
   * 查询+分页
   *
   * @param goods
   * @param page
   * @param rows
   * @return
   */
  @RequestMapping("/search")
  public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
    return goodsService.findPage(goods, page, rows);
  }
	
	/*@Reference
	private ItemSearchService itemSearchService;*/

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private Destination queueSolrDestination; // 用于导入消息索引库的消息目标 点对点

  @Autowired
  private Destination topicPageDestination; // 用户生成商品详情页的消息目标 发布/订阅

  /**
   * @methodName:updateStatus
   * @description: 更新商品状态
   * @author：Xiaobai
   * @createTime：2019年7月30日 下午4:01:33
   * @remarks: @param ids
   * @remarks: @param status
   * @remarks: @return
   * @resultType：Result
   */
  @RequestMapping("/updateStatus")
  public Result updateStatus(Long[] ids, String status) {
    try {

      goodsService.updateStatus(ids, status);
      if ("1".equals(status)) { // 商品审核通过
        // 获得导入的SKU列表
        List<TbItem> itemList = goodsService.findItemListByGoodsIdListAndStatus(ids, status);
        String jsonString = JSON.toJSONString(itemList); // 转换成JSON字符串，作为参数传递
        // 导入到solr
        // itemSearchService.importList(itemList);

        jmsTemplate.send(queueSolrDestination, new MessageCreator() {

          @Override
          public Message createMessage(Session session) throws JMSException {

            return session.createTextMessage(jsonString);
          }
        });

        // 生成商品详情页
        for (Long goodsId : ids) {
          // itemPageService.genItemHtml(goodsId);
          jmsTemplate.send(topicPageDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {

              return session.createTextMessage(goodsId + "");
            }
          });
        }

      }

      return new Result(true, "更新成功");
    } catch (Exception e) {
      e.printStackTrace();
      return new Result(false, "更新失败");

    }
  }
	
	/*@Reference
	private ItemPageService itemPageService;*/

  /**
   * @methodName:genHtml
   * @description: 生成静态页测试
   * @author：Xiaobai
   * @createTime：2019年8月6日 下午2:48:00
   * @remarks: @param goodsId
   * @resultType：void
   */
  @RequestMapping("/genHtml")
  public void genHtml(Long goodsId) {
    // itemPageService.genItemHtml(goodsId);
  }

}
