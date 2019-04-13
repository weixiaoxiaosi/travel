package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 请求处理器
 * @author Steven
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(username);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//验证修改权限，商家只能修改自己的商品
		Goods serviceOne = goodsService.findOne(goods.getGoods().getId());
		//获取当前登录用户的id
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		//如果当前修改的商品不是当前登录商家的,提示操作非法
		if (!name.equals(serviceOne.getGoods().getSellerId())||
				!name.equals(goods.getGoods().getSellerId())){
			return new Result(false, "非法操作！！！");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(username);
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	* @Date 14:24 2019/3/20
	* 是否上架
	* @Param [isMarketable]
	* @return com.pinyougou.entity.Result
	**/
	@RequestMapping(value = "/updateIsMarketable")
	public Result updateIsMarketable(Long[] ids ,String isMarketable){
		try {
			goodsService.updateIsMarketable(ids,isMarketable);
			System.out.println(isMarketable);
			return new Result(true,"操作成功!!!" );

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(false,"操作失败" );
	}
}
