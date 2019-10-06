package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

  @Autowired
  private TbBrandMapper brandMapper;

  @Override
  public List<TbBrand> findAll() {
    return brandMapper.selectByExample(null);

  }


  @Override
  public PageResult findPage(int pageNum, int pageSize) {
    // 分页
    PageHelper.startPage(pageNum, pageSize);
    Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
    return new PageResult(page.getTotal(), page.getResult());
  }


  @Override
  public boolean add(TbBrand brand) {
    // 尝试设置不可添加已有品牌
    List<TbBrand> selectByExample = brandMapper.selectByExample(null);
    boolean flag = false;
    for (TbBrand tbBrand : selectByExample) {
      if (tbBrand.getName().equals(brand.getName())) { // 数据库中已有该品牌
        flag = true;
      }
    }
    if (flag) {
      return true;
    } else {
      brandMapper.insert(brand);
      return false;
    }
  }


  @Override
  public TbBrand findOne(Long id) {
    return brandMapper.selectByPrimaryKey(id);
  }


  @Override
  public void update(TbBrand brand) {
    brandMapper.updateByPrimaryKey(brand);

  }

  @Override
  public void delete(Long[] ids) {
    for (Long id : ids) {
      brandMapper.deleteByPrimaryKey(id);
    }

  }


  @Override
  public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
    PageHelper.startPage(pageNum, pageSize); // 分页
    try {
      // if (brand != null) {
      TbBrandExample example = new TbBrandExample();
      Criteria criteria = example.createCriteria();
      if (brand.getName() != null && brand.getName().length() > 0) {
        criteria.andNameLike("%" + brand.getName() + "%");
      }
      if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
        criteria.andFirstCharLike(brand.getFirstChar());
      }
      Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
      return new PageResult(page.getTotal(), page.getResult());
      //}
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }


  }


  @Override
  public List<Map> selectOptionList() {
    return brandMapper.selectOptionList();
  }


}
