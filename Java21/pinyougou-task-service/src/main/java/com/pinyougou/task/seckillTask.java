package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class seckillTask {

  @Autowired
  private RedisTemplate redisTemplate;

  @Autowired
  private TbSeckillGoodsMapper seckillGoodsMapper;

  /**
   * @methodName:refreshSeckillGoods
   * @description: 刷新秒杀商品
   * @author：Xiaobai
   * @createTime：2019年8月17日 上午11:34:43
   * @remarks:
   * @resultType：void
   */
  @Scheduled(cron = "0 * * * * ?")
  public void refreshSeckillGoods() {

    System.out.println("执行了秒杀商品增量更新 任务调度：" + new Date());
    // 查询缓存中的秒杀商品ID集合
    List goodIdList = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
    System.out.println(goodIdList);
    TbSeckillGoodsExample example = new TbSeckillGoodsExample();
    Criteria criteria = example.createCriteria();
    criteria.andStatusEqualTo("1"); // 审核通过
    criteria.andStockCountGreaterThan(0); // 库存大于0
    criteria.andStartTimeLessThan(new Date()); // 开始时间小于当前时间
    criteria.andEndTimeGreaterThanOrEqualTo(new Date()); // 结束时间大于当前时间
    if (goodIdList.size() > 0) {
      criteria.andIdNotIn(goodIdList); // 排除缓存中已经有的商品
    }
    List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

    // 将列表数据装入缓存中
    for (TbSeckillGoods seckillGoods : seckillGoodsList) {
      redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
      System.out.println("增量更新商品id:" + seckillGoods.getId());
    }
    System.out.println("-------结束----------");

  }


  /**
   * @methodName:removeSeckillGoods
   * @description: 清除秒杀商品
   * @author：Xiaobai
   * @createTime：2019年8月17日 下午3:28:48
   * @remarks:
   * @resultType：void
   */
  @Scheduled(cron = "* * * * * ?")
  public void removeSeckillGoods() {
    List<TbSeckillGoods> seckillGoodList = redisTemplate.boundHashOps("seckillGoods").values();
    System.out.println("执行清除秒杀商品任务" + new Date());
    for (TbSeckillGoods seckillGoods : seckillGoodList) {
      if (seckillGoods.getEndTime().getTime() < new Date().getTime()) {
        // 同步到数据库
        seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
        // 清除缓存
        redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
        System.out.println("秒杀商品已过期" + seckillGoods.getId());
      }
    }
    System.out.println("执行了清除秒杀商品任务---------------end");

  }


}
