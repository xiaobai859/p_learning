package com.pinyougou.seckill.service;

import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

  /**
   * 返回全部列表
   *
   * @return
   */
  public List<TbSeckillOrder> findAll();


  /**
   * 返回分页列表
   *
   * @return
   */
  public PageResult findPage(int pageNum, int pageSize);


  /**
   * 增加
   */
  public void add(TbSeckillOrder seckillOrder);


  /**
   * 修改
   */
  public void update(TbSeckillOrder seckillOrder);


  /**
   * 根据ID获取实体
   *
   * @param id
   * @return
   */
  public TbSeckillOrder findOne(Long id);


  /**
   * 批量删除
   *
   * @param ids
   */
  public void delete(Long[] ids);

  /**
   * 分页
   *
   * @param pageNum  当前页 码
   * @param pageSize 每页记录数
   * @return
   */
  public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

  /**
   * @methodName:submitOrder
   * @description: 秒杀订单提交
   * @author：Xiaobai
   * @createTime：2019年8月16日 下午8:07:58
   * @remarks: @param sellerId
   * @remarks: @param userId
   * @resultType：void
   */
  public void submitOrder(Long seckillId, String userId);


  /**
   * @methodName:searchOrderFromRedis
   * @description: 从缓存中查询订单
   * @author：Xiaobai
   * @createTime：2019年8月17日 上午9:06:20
   * @remarks: @param userId
   * @remarks: @return
   * @resultType：TbSeckillOrder
   */
  public TbSeckillOrder searchOrderFromRedis(String userId);


  /**
   * @methodName:saveOrderFromRedisToDB
   * @description: 将缓存中订单存到数据库
   * @author：Xiaobai
   * @createTime：2019年8月17日 上午9:30:49
   * @remarks: @param userId
   * @remarks: @param orderId
   * @remarks: @param transactionId
   * @resultType：void
   */
  public void saveOrderFromRedisToDB(String userId, Long orderId, String transaction_id);

  /**
   * @methodName:deleteOrderFromRedis
   * @description: 订单超时，从缓存中删除订单
   * @author：Xiaobai
   * @createTime：2019年8月17日 上午10:17:54
   * @remarks: @param userId
   * @remarks: @param orderId
   * @resultType：void
   */
  public void deleteOrderFromRedis(String userId, Long orderId);


}
