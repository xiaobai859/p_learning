package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

/**
 * "
 *
 * @version V1.0
 * @typeName: Specification
 * @classDesc: 规格组合实体类
 * @author：XiaoBai
 * @createTime：2019年7月20日 下午4:02:01
 * @remarks：
 */
public class Specification implements Serializable {

  private TbSpecification specification;

  private List<TbSpecificationOption> specificationOptionList;

  public TbSpecification getSpecification() {
    return specification;
  }

  public void setSpecification(TbSpecification specification) {
    this.specification = specification;
  }

  public List<TbSpecificationOption> getSpecificationOptionList() {
    return specificationOptionList;
  }

  public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
    this.specificationOptionList = specificationOptionList;
  }


}
