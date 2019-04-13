package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import java.util.List;


/**
 * 业务逻辑接口
 * @author Steven
 *
 */
public interface GoodsService {

	/**
	* @Date 14:51 2019/3/16
	* 增加
	* @Param [goods]
	* @return void
	**/
	public void add(Goods goods);

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();

	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
//	public void add(TbGoods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	* @Date 14:47 2019/3/19
	* 通过id修改goods里面的审核状态
	* @Param [ids, status]
	* @return void
	**/
	public void updateStatus(Long[] ids,String status);

	/**
	* @Date 10:18 2019/3/20
	* 是否上架
	* @Param [isMarketable]
	* @return void
	**/

    public void updateIsMarketable(Long[] ids ,String isMarketable);


	/**
	 * 跟据SPU-ID列表和状态，查询SKU列表
	 * @param ids
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);
}
