package com.pinyougou.seckill.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbSeckillOrder> result = new PageResult<TbSeckillOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbSeckillOrder> list = seckillOrderMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insertSelective(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKeySelective(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        seckillOrderMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageResult<TbSeckillOrder> result = new PageResult<TbSeckillOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						//如果字段不为空
			if (seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0) {
				criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0) {
				criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0) {
				criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0) {
				criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0) {
				criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0) {
				criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
			}
			//如果字段不为空
			if (seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0) {
				criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
			}
	
		}

        //查询数据
        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	/**
	* @Date 2019/4/8 8:59
	* 提交下单
	* @Param [seckillId, userId]
	* @return void
	**/
    @Override
    public synchronized  void submitOrder(Long seckillId, String userId) {
    	//从redis里面拿数据
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if (seckillGoods==null){
			throw new RuntimeException("商品不存在!");
		}if (seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已抢购一空!");
		}
		//扣减库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods );

		new Thread(){
			@Override
			public void run() {
				//当库存不足时
				if (seckillGoods.getStockCount()==0){
					//更新数据库
					seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
					//删除缓存
					redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods);
				}
				//在支付之前，保存订单到redis
				TbSeckillOrder seckillOrder = new TbSeckillOrder();
				seckillOrder.setId(idWorker.nextId());//保存支付订单号
				seckillOrder.setSeckillId(seckillId);//保存秒杀商品ID
				seckillOrder.setMoney(seckillGoods.getCostPrice());//保存支付金额
				seckillOrder.setUserId(userId);//保存用户id
				seckillOrder.setSellerId(seckillGoods.getSellerId());//保存商家Id
				seckillOrder.setCreateTime(new Date());//保存创建时间
				seckillOrder.setStatus("0");//状态
				//保存订单到redis
				redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder );
				super.run();
			}
		}.start();

	}

	/**
	* @Date 2019/4/8 9:39
	* 根据用户名查询订单
	* @Param [userId]
	* @return com.pinyougou.pojo.TbSeckillOrder
	**/
    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		return seckillOrder;
    }

    /**
    * @Date 2019/4/8 9:46
    * 支付成功保存订单
    * @Param [userId, orderId, transactionId]
    * @return void
    **/
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {

		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (seckillOrder==null){
			throw new RuntimeException("订单不存在");
		}if (orderId.longValue()!=seckillOrder.getId().longValue()){
			throw new RuntimeException("订单号不匹配");
		}

		seckillOrder.setTransactionId(transactionId);//保存交易流水号
		seckillOrder.setPayTime(new Date());//保存实付时间
		seckillOrder.setStatus("1");//状态
		//保存到数据库
		seckillOrderMapper.insertSelective(seckillOrder);

		//从缓存中删除
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

	/**
	* @Date 2019/4/8 10:08
	* 从缓存中释放订单
	* @Param [userId, orderId]
	* @return void
	**/
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (seckillOrder!= null && orderId.longValue() == seckillOrder.getId().longValue()){
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
			//恢复库存
			//1.从缓存中提取秒杀商品
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
			seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
			//恢复库存
			redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
		}

    }

}
