package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum,int pageSize);
	
	/**
	 * @methodName:createCheckCode
	 * @description: 生成验证码并发送
	 * @author：Xiaobai
	 * @createTime：2019年8月9日 下午9:12:05
	 * @remarks: @param phone
	 * @resultType：void
	 */
	public void createCheckCode(String phone);
	
	/**
	 * @methodName:checkCodeIsTrue
	 * @description: 验证用户输入的验证码是否正确
	 * @author：Xiaobai
	 * @createTime：2019年8月10日 上午9:00:59
	 * @remarks: @param phone
	 * @remarks: @param checkCode
	 * @remarks: @return
	 * @resultType：boolean
	 */
	public boolean checkCodeIsTrue(String phone, String checkCode);
	
}
