package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * @version V1.0
 * @typeName: BrandService
 * @classDesc: 品牌接口
 * @author：XiaoBai
 * @createTime：2019年7月17日 下午6:44:50
 * @remarks：
 */
public interface BrandService {

  public List<TbBrand> findAll();

  /**
   * @methodName:findPage
   * @description: 品牌分页
   * @author：Xiaobai
   * @createTime：2019年7月18日 上午10:24:38
   * @remarks: @param pageNum  当前页
   * @remarks: @param pageSize 每页记录数
   * @remarks: @return
   * @resultType：PageResult
   */
  public PageResult findPage(int pageNum, int pageSize);


  /**
   * @methodName:add
   * @description: 商品添加
   * @author：Xiaobai
   * @createTime：2019年7月18日 下午2:18:02
   * @remarks: @param brand
   * @resultType：Boolean
   */
  public boolean add(TbBrand brand);

  /**
   * @methodName:findOne
   * @description: 根据id查找品牌
   * @author：Xiaobai
   * @createTime：2019年7月18日 下午4:20:24
   * @remarks: @param id
   * @remarks: @return
   * @resultType：TbBrand
   */
  public TbBrand findOne(Long id);

  /**
   * @methodName:update
   * @description: 修改品牌信息
   * @author：Xiaobai
   * @createTime：2019年7月18日 下午4:23:41
   * @remarks: @param brand
   * @resultType：void
   */
  public void update(TbBrand brand);

  /**
   * @methodName:delete
   * @description: 删除所选条目品牌
   * @author：Xiaobai
   * @createTime：2019年7月18日 下午6:52:10
   * @remarks: @param ids 选中的所有品牌
   * @resultType：void
   */
  public void delete(Long[] ids);

  /**
   * @methodName:search
   * @description: TODO
   * @author：Xiaobai
   * @createTime：2019年7月18日 下午8:14:42
   * @remarks: @param brand 把查询条件封装为对象传递
   * @remarks: @param page
   * @remarks: @param size
   * @remarks: @return
   * @resultType：PageResult
   */
  public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

  /**
   * @methodName:selectOptionList
   * @description: 返回下拉列表数据
   * @author：Xiaobai
   * @createTime：2019年7月21日 下午5:15:41
   * @remarks: @return
   * @resultType：List<Map>
   */
  public List<Map> selectOptionList();


}
