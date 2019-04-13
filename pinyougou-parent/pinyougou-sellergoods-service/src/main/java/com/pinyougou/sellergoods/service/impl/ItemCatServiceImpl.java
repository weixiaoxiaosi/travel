package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 5000)
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	* @Date 14:14 2019/3/14
	* 通过parentId查找一级分类
	* @Param [parentId]
	* @return java.util.List<com.pinyougou.pojo.TbItemCat>
	**/
	@Override
	public List<TbItemCat> findByParentId(Long parentId) {

		TbItemCat where = new TbItemCat();
		where.setParentId(parentId);

		List<TbItemCat> tbItemCats = itemCatMapper.select(where);
		//将商品分类数据放入缓存（Hash）。以分类名称作为key ,以模板ID作为值
		//在这里写的原因是商品分类增删改都会经过这个方法
		//查询所有itemCat
		List<TbItemCat> itemCats = findAll();
		for (TbItemCat itemCat : itemCats) {
			redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
		}
		return tbItemCats;
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbItemCat> result = new PageResult<TbItemCat>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbItemCat> list = itemCatMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insertSelective(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKeySelective(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
		for (Long id : ids) {
			List list=new ArrayList();
			list.add(id);
			selectAllDelete(list,id);
			TbItemCat where = new TbItemCat();
			where.setId(id);
			itemCatMapper.delete(where);
		}

	}

	public void selectAllDelete(List list ,Long id){
		TbItemCat where = new TbItemCat();
		where.setParentId(id);
		List<TbItemCat> select = itemCatMapper.select(where);
		if (select!=null&&select.size()>0){
			for (TbItemCat itemCat : select) {
				itemCatMapper.delete(itemCat);
				selectAllDelete(list,itemCat.getId());
			}
		}else {
			return;
		}
	}

	
	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageResult<TbItemCat> result = new PageResult<TbItemCat>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						//如果字段不为空
			if (itemCat.getName()!=null && itemCat.getName().length()>0) {
				criteria.andLike("name", "%" + itemCat.getName() + "%");
			}
	
		}

        //查询数据
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}
	
}
