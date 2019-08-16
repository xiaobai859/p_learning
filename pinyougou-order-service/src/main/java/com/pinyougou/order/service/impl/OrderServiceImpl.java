package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired 
	private TbOrderItemMapper orderItemMapper;
	
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		List<String> orderIdList = new ArrayList<String>(); // 订单id列表
		double total_money = 0; // 总金额（元）
		
		for (Cart cart : cartList) {
			
			long orderId = idWorker.nextId();
			System.out.println(orderId);
			System.out.println("sellerId:"+cart.getSellerId());
			TbOrder tbOrder = new TbOrder(); // 新创建订单对象
			tbOrder.setOrderId(orderId); // 订单ID
			tbOrder.setUserId(order.getUserId()); // 用户名
			tbOrder.setPaymentType(order.getPaymentType()); // 支付类型
			tbOrder.setStatus("1"); // 状态：未付款
			tbOrder.setCreateTime(new Date()); // 订单创建日期
			tbOrder.setUpdateTime(new Date()); // 订单更新日期
			tbOrder.setReceiverAreaName(order.getReceiverAreaName()); // 地址
			tbOrder.setReceiverMobile(order.getReceiverMobile()); // 手机号
			tbOrder.setReceiver(order.getReceiver()); // 收货人
			tbOrder.setSourceType(order.getSourceType()); // 订单来源
			tbOrder.setSellerId(cart.getSellerId()); // 商家ID	
			double money = 0;
			
			for(TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItem.setSellerId(cart.getSellerId());
				money += orderItem.getTotalFee().doubleValue();
				orderItemMapper.insert(orderItem);
			}
			tbOrder.setPayment(new BigDecimal(money));
			orderMapper.insert(tbOrder);
			
			orderIdList.add(orderId + ""); // 提交订单将其添加到订单列表
			total_money += money; // 订单总金额
		}
		System.out.println("订单总金额——————————————————" + total_money);
		// 添加支付日志
		if("1".equals(order.getPaymentType())) { // 如果支付类型是微信支付
			TbPayLog payLog = new TbPayLog();
			payLog.setCreateTime(new Date()); // 创建时间
			payLog.setOutTradeNo(idWorker.nextId() + ""); // 支付订单
			payLog.setPayType("1"); // 支付类型
			// 订单列表用逗号分隔
			String orders = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", ",");
			payLog.setOrderList(orders); // 订单列表
			payLog.setTotalFee((long) (total_money * 100)); // 总金额
			System.out.println(payLog.getTotalFee().longValue()+ "添加支付日志金额");
			payLog.setTradeState("0"); // 支付状态
			payLog.setUserId(order.getUserId()); // 用户ID
			
			payLogMapper.insert(payLog); // 插入到支付日志表中
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
		}
		
		
		
		
		// 清空redis缓存
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public TbPayLog searchPayLogFromRedis(String userId) {
			return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		}

		@Override
		public void updateOrderStatus(String out_trade_no, String transaction_id) {
			// 修改支付日志表中支付状态及时间等相关字段
			TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
			payLog.setPayTime(new Date()); // 支付时间
			payLog.setTransactionId(transaction_id);  // 微信交易流水号
			payLog.setTradeState("1"); // 交易状态
			payLogMapper.updateByPrimaryKey(payLog);
			
			// 修改订单表状态
			String orderListStr = payLog.getOrderList(); // 获取订单id串, 通过逗号分隔的
			String[] orderIdList = orderListStr.split(","); // 获取订单id
			for (String orderId : orderIdList) {
				TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
				order.setPaymentTime(new Date());
				order.setStatus("2"); // 设置订单状态为已支付
				orderMapper.updateByPrimaryKey(order); // 更新订单状态
			}
			
			// 清除缓存中payLog
			redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
			
		}
	
}
