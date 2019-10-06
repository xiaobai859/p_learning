package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

  /**
   * @methodName:search
   * @description: 搜索方法
   * @author：Xiaobai
   * @createTime：2019年8月2日 下午7:42:28
   * @remarks: @param searchMap
   * @remarks: @return
   * @resultType：Map
   */
  public Map search(Map searchMap);

  /**
   * @methodName:importList
   * @description: 批量导入列表
   * @author：Xiaobai
   * @createTime：2019年8月5日 下午4:13:52
   * @remarks: @param list
   * @resultType：void
   */
  public void importList(List list);

  /**
   * @methodName:deleteByGoodsIds
   * @description: 商品删除
   * @author：Xiaobai
   * @createTime：2019年8月5日 下午6:30:55
   * @remarks: @param list
   * @resultType：void
   */
  public void deleteByGoodsIds(List list);

}
