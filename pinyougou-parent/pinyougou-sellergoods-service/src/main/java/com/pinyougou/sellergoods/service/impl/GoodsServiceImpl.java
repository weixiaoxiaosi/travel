package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(interfaceClass = GoodsService.class)
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbBrandMapper brandMapper;


	/**
    * @Date 2019/3/25 16:33
    * 新增tb_goods
    * @Param [goods]
    * @return void
    **/

    @Override
	public void add(Goods goods) {

		//新增tb_goods里面的内容
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insertSelective(goods.getGoods());

		//新增tb_goods_desc里面的内容
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insertSelective(goods.getGoodsDesc());

		saveItemList(goods);


	}

	private void saveItemList(Goods goods) {
		//新增tb_item里面的内容
		if ("1".equals(goods.getGoods().getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				//标题由SPU+ SKU列表
				String title = goods.getGoods().getGoodsName();
				Map<String,String> map= JSON.parseObject(tbItem.getSpec(), Map.class);

				for (String key : map.values()) {
					title=title+" "+key;
				}
				tbItem.setTitle(title);
				setItemValus(goods, tbItem);

				//保存SKU
				itemMapper.insertSelective(tbItem);
			}
		}else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			//设置详细属性
			setItemValus(goods, item);
			//保存sku信息
			itemMapper.insertSelective(item);
		}
	}

	private void setItemValus(Goods goods, TbItem tbItem) {
		//商品图片取SPU的第一张
		List<Map> images = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(images.size() > 0){
			tbItem.setImage(images.get(0).get("url").toString());
		}
		//商品类目id
		tbItem.setCategoryid(goods.getGoods().getCategory3Id());
		//查询商品类目内容
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid());
		tbItem.setCategory(tbItemCat.getName());
		//创建日期
		tbItem.setCreateTime(new Date());
		//更新日期
		tbItem.setUpdateTime(tbItem.getCreateTime());
		//所属SPU-id
		tbItem.setGoodsId(goods.getGoods().getId());
		//所属商家
		tbItem.setSellerId(goods.getGoods().getSellerId());
		TbSeller seller = sellerMapper.selectByPrimaryKey(tbItem.getSellerId());
		tbItem.setSeller(seller.getName());
		//品牌信息
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		tbItem.setBrand(brand.getName());
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbGoods> result = new PageResult<TbGoods>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbGoods> list = goodsMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(list);
        result.setTotal(info.getTotal());
		return result;
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//修改过的商品，状态设置为未审核，重新审核一次
		goods.getGoods().setAuditStatus("0");
		//更新商品基本信息
		goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
		//更新商品扩展信息
		goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
		//更新sku信息，更新前先删除原来的sku
		TbItem where =new TbItem();
		where.setGoodsId(goods.getGoods().getId());
		itemMapper.delete(where);
		//保存新的SKU
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//查找tb_goods 里面的数据
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);

		//查找tb_goods_desc里面的数据
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		//查找tb_item里面的数据
		TbItem where = new TbItem();
		where.setGoodsId(id);
		List<TbItem> select = itemMapper.select(where);
		goods.setItemList(select);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageResult<TbGoods> result = new PageResult<TbGoods>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						//如果字段不为空
			if (goods.getSellerId()!=null && goods.getSellerId().length()>0) {
//				criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
				criteria.andEqualTo("sellerId", goods.getSellerId());
			}
			//如果字段不为空
			if (goods.getGoodsName()!=null && goods.getGoodsName().length()>0) {
				criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
			}
			//如果字段不为空
			if (goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0) {
				criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
			}
			//如果字段不为空
			if (goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0) {
				criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
			}
			//如果字段不为空
			if (goods.getCaption()!=null && goods.getCaption().length()>0) {
				criteria.andLike("caption", "%" + goods.getCaption() + "%");
			}
			//如果字段不为空
			if (goods.getSmallPic()!=null && goods.getSmallPic().length()>0) {
				criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
			}
			//如果字段不为空
			if (goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0) {
				criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
			}
			//如果字段不为空
//			if (goods.getIsDelete()!=null && goods.getIsDelete().length()>0) {
//				criteria.andLike("isDelete", "%" + goods.getIsDelete() + "%");
//			}
	        criteria.andIsNull("isDelete");
		}

        //查询数据
        List<TbGoods> list = goodsMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	/**
	* @Date 14:48 2019/3/19
	* 通过id修改goods里面的审核状态
	* @Param [ids, status]
	* @return void
	**/
	@Override
	public void updateStatus(Long[] ids, String status) {
		TbGoods goods = new TbGoods();
		goods.setAuditStatus(status);
		Example where = new Example(TbGoods.class);
		Example.Criteria criteria = where.createCriteria();
        List longs = Arrays.asList(ids);
        criteria.andIn("id",longs);
		goodsMapper.updateByExampleSelective(goods,where);
	}

	/**
	* @Date 14:24 2019/3/20
	* 是否上架
	* @Param [isMarketable]
	* @return void
	**/
	@Override
	public void updateIsMarketable(Long[] ids ,String isMarketable) {
		for (Long id : ids) {
			TbGoods where = new TbGoods();
			where.setIsMarketable(isMarketable);
			where.setId(id);
			goodsMapper.updateByPrimaryKeySelective(where);

		}

	}



	/**
	 * 跟据SPU-ID列表和状态，查询SKU列表
	 * @param ids
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status){

		Example example = new Example(TbItem.class);
		Example.Criteria criteria = example.createCriteria();
		List longs = Arrays.asList(ids);
		criteria.andIn("goodsId",longs);
		criteria.andEqualTo("status", status);

		List<TbItem> items = itemMapper.selectByExample(example);
		return items;
	}

}
