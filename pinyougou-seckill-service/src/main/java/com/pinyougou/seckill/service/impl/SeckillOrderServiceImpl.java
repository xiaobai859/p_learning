package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.sound.midi.Soundbank;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private IdWorker idWorker;
	
	@Override
	public void submitOrder(Long seckillId, String userId) {
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		
		if(seckillGoods == null) {
			throw new RuntimeException("商品不存在");
		}
		if(seckillGoods.getStockCount() == 0) {
			throw new RuntimeException("商品已售罄");
			
		}
		// 减少库存
		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1); // 库存减1
		redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods); // 重新存入缓存
		if(seckillGoods.getStockCount() == 0) {
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods); // 更新数据库
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId); // 清除缓存
			System.out.println("将商品从缓存同步到数据库");
		}
		
		// 存储秒杀订单（先往缓存中存储，支付后才存入数据库）
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setId(idWorker.nextId());
		seckillOrder.setSeckillId(seckillId);
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setUserId(userId);
		seckillOrder.setSellerId(seckillGoods.getSellerId());;
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setStatus("0");
		
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
		System.out.println("保存秒杀订单到redis缓存中");
	}

	
	@Override
	public TbSeckillOrder searchOrderFromRedis(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	// 缓存中订单存入到数据库
	@Override
	public void saveOrderFromRedisToDB(String userId, Long orderId, String transaction_id) {
		System.out.println("saveOrderFromRedisToDB:"+userId);
		// 从缓存中提取订单数据
		TbSeckillOrder seckillOrder = searchOrderFromRedis(userId);
		if(seckillOrder == null) {
			throw new RuntimeException("订单不存在");
		}
		// 校验订单号是否相同
		if(seckillOrder.getId().longValue() != orderId.longValue()) {
			throw new RuntimeException("订单不相符");
		}
		// 修改订单实体属性
		seckillOrder.setPayTime(new Date()); // 支付时间
		seckillOrder.setStatus("1"); // 支付状态 1：已支付
		System.out.println("秒杀商品ID：" + seckillOrder.getSeckillId());
		seckillOrder.setTransactionId(transaction_id); // 微信交易流水号
		// 将订单存入数据库
		seckillOrderMapper.insert(seckillOrder);
		// 清除缓存
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
		
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		// 从缓存中提取订单数据
		TbSeckillOrder seckillOrder = searchOrderFromRedis(userId);
		if(seckillOrder != null && seckillOrder.getId().longValue() == orderId) {
			// 从缓存中删除订单
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
			
			// 恢复库存
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
			if(seckillGoods != null) {
				seckillGoods.setStockCount(seckillGoods.getStockCount() + 1); // 库存数+1
				// 重新存入缓存
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
			} /*else {// 库存秒杀完了  用户超时未支付
				seckillGoods = new TbSeckillGoods();
				seckillGoods.setStockCount(1);
				seckillGoods.setId(seckillOrder.getSeckillId());
				seckillGoods.setCostPrice(seckillOrder.getMoney());
				
				
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
			}*/
			
			System.out.println("取消订单" + orderId);
		}
		
	}
	
	
	
	
}
