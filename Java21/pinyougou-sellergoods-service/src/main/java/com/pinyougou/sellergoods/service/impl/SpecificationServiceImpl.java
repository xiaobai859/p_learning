package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

  @Autowired
  private TbSpecificationMapper specificationMapper;

  @Autowired
  private TbSpecificationOptionMapper specificaionOptionMapper;

  /**
   * 查询全部
   */
  @Override
  public List<TbSpecification> findAll() {
    return specificationMapper.selectByExample(null);
  }

  /**
   * 按分页查询
   */
  @Override
  public PageResult findPage(int pageNum, int pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
    return new PageResult(page.getTotal(), page.getResult());
  }

  /**
   * 增加
   */
  @Override
  public void add(Specification specification) {
    // 获取规格实体
    TbSpecification tbSpecification = specification.getSpecification();
    specificationMapper.insert(tbSpecification);
    // 获取规格选项集合
    List<TbSpecificationOption> specificationOptionList = specification
        .getSpecificationOptionList();
    for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
      // 先获取刚插入到tb_specification表找的主键id
      tbSpecificationOption.setSpecId(tbSpecification.getId());
      specificaionOptionMapper.insert(tbSpecificationOption);
    }

  }


  /**
   * 修改
   */
  @Override
  public void update(Specification specification) {
    // 获取规格实体
    TbSpecification tbSpecification = specification.getSpecification();
    specificationMapper.updateByPrimaryKey(tbSpecification);

    // 需要先删除原来的规格选项
    TbSpecificationOptionExample example = new TbSpecificationOptionExample();
    com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
    criteria.andSpecIdEqualTo(tbSpecification.getId());
    specificaionOptionMapper.deleteByExample(example);

    // 再重新获取规格选项集合
    List<TbSpecificationOption> specificationOptionList = specification
        .getSpecificationOptionList();
    for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
      tbSpecificationOption.setSpecId(tbSpecification.getId());
      specificaionOptionMapper.insert(tbSpecificationOption);
    }
  }

  /**
   * 根据ID获取实体
   *
   * @param id
   * @return
   */
  @Override
  public Specification findOne(Long id) {

    Specification specification = new Specification();
    // 获取组合实体类 TbSpecification(规格实体) List<TbSpecificationOption>(规格选项实体)
    TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
    // 获取查询条件
    TbSpecificationOptionExample example = new TbSpecificationOptionExample();

    com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
    criteria.andSpecIdEqualTo(id);
    // 上面两句合为一句 // example.createCriteria().andSpecIdEqualTo(id);
    List<TbSpecificationOption> specificationOptionList = specificaionOptionMapper
        .selectByExample(example);
    // 封装到specification
    specification.setSpecification(tbSpecification);
    specification.setSpecificationOptionList(specificationOptionList);
    return specification;
  }

  /**
   * 批量删除
   */
  @Override
  public void delete(Long[] ids) {
    for (Long id : ids) {
      // 删除规格表
      specificationMapper.deleteByPrimaryKey(id);

      // 删除规格选项表
      TbSpecificationOptionExample example = new TbSpecificationOptionExample();
      example.createCriteria().andSpecIdEqualTo(id);
      specificaionOptionMapper.deleteByExample(example);
    }
  }


  @Override
  public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
    PageHelper.startPage(pageNum, pageSize);

    TbSpecificationExample example = new TbSpecificationExample();
    Criteria criteria = example.createCriteria();

    if (specification != null) {
      if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
        criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
      }

    }

    Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper
        .selectByExample(example);
    return new PageResult(page.getTotal(), page.getResult());
  }

  @Override
  public List<Map> selectOptionList() {
    return specificationMapper.selectOptionList();
  }

}
