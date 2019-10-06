package com.pinyougou.sellergoods.service;

import java.util.List;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService {

  /**
   * 返回全部列表
   *
   * @return
   */
  public List<TbGoods> findAll();


  /**
   * 返回分页列表
   *
   * @return
   */
  public PageResult findPage(int pageNum, int pageSize);


  /**
   * 增加
   */
  public void add(Goods goods);


  /**
   * 修改
   */
  public void update(Goods goods);


  /**
   * 根据ID获取实体
   *
   * @param id
   * @return
   */
  public Goods findOne(Long id);


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
  public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

  /**
   * @methodName:updateStatus
   * @description: 更新商品状态(是否删除)
   * @author：Xiaobai
   * @createTime：2019年7月30日 下午3:51:43
   * @remarks: @param ids
   * @remarks: @param status
   * @resultType：void
   */
  public void updateStatus(Long[] ids, String status);

  /**
   * @methodName:goodsMark
   * @description: 跟新商品上下架状态 0：下架    1：上架
   * @author：Xiaobai
   * @createTime：2019年7月30日 下午5:59:59
   * @remarks: @param ids
   * @remarks: @param isMarketable
   * @resultType：void
   */
  public void goodsMark(Long[] ids, String isMarketable);

  /**
   * @methodName:findItemListByGoodsIdListAndStatus
   * @description: 根据SPU的id查询SKU列表
   * @author：Xiaobai
   * @createTime：2019年8月5日 下午4:03:02
   * @remarks: @param ids
   * @remarks: @param status
   * @remarks: @return
   * @resultType：List<TbItem>
   */
  public List<TbItem> findItemListByGoodsIdListAndStatus(Long[] ids, String status);

}
