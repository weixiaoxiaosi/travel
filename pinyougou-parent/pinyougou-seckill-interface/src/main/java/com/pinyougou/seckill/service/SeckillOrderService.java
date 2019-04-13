package com.pinyougou.seckill.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbSeckillOrder;

import java.util.List;

/**
 * 业务逻辑接口
 * @author Steven
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
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
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);


	/**
	* @Date 2019/4/8 8:58
	* 提交下单
	* @Param [seckillId, userId]
	* @return void
	**/
	public void submitOrder(Long seckillId,String userId);

	/**
	* @Date 2019/4/8 9:39
	* 根据用户名查询订单
	* @Param [userId]
	* @return com.pinyougou.pojo.TbSeckillOrder
	**/
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);
	
	/**
	* @Date 2019/4/8 9:46
	* 支付成功保存订单
	* @Param [userId, orderId, transactionId]
	* @return void
	**/ 
	public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);

	/**
	* @Date 2019/4/8 10:08
	* 从缓存中释放订单
	* @Param [userId, orderId]
	* @return void
	**/
	public void deleteOrderFromRedis(String userId,Long orderId);
}
