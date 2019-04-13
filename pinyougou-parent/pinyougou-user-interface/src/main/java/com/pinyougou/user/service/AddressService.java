package com.pinyougou.user.service;
import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbAddress;

import java.util.List;

/**
 * 业务逻辑接口
 * @author Steven
 *
 */
public interface AddressService {

    /**
    * @Date 2019/4/2 10:14
    * 根据用户查询地址
    * @Param [UserId]
    * @return java.util.List<com.pinyougou.pojo.TbAddress>
    **/
    public List<TbAddress> findListByUserId (String UserId);


	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbAddress> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	public void update(TbAddress address);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbAddress findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param id
	 */
	public void delete(Long id);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbAddress address, int pageNum, int pageSize);

	/**
	* @Date 2019/4/7 11:03
	* 设置默认地址
	* @Param [id]
	* @return void
	**/
	public void install(Long id ,String userId);
}
