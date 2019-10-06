package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbTypeTemplate;

import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface TypeTemplateService {

  /**
   * 返回全部列表
   *
   * @return
   */
  public List<TbTypeTemplate> findAll();


  /**
   * 返回分页列表
   *
   * @return
   */
  public PageResult findPage(int pageNum, int pageSize);


  /**
   * 增加
   */
  public void add(TbTypeTemplate typeTemplate);


  /**
   * 修改
   */
  public void update(TbTypeTemplate typeTemplate);


  /**
   * 根据ID获取实体
   *
   * @param id
   * @return
   */
  public TbTypeTemplate findOne(Long id);


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
  public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize);

  /**
   * @methodName:selectOptionList
   * @description: 查询类型模板
   * @author：Xiaobai
   * @createTime：2019年7月27日 下午12:26:11
   * @remarks: @return
   * @resultType：List<Map>
   */
  public List<Map> selectOptionList();

  /**
   * @methodName:findSpeList
   * @description: 查询规格列表
   * @author：Xiaobai
   * @createTime：2019年7月28日 下午4:23:54
   * @remarks: @param id
   * @remarks: @return
   * @resultType：List<Map>
   */
  public List<Map> findSpecList(Long id);
}
