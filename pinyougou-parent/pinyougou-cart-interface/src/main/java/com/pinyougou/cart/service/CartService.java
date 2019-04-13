package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * @author shenliang
 * @version 1.0
 * 购物车服务接口
 * @description com.pinyougou.cart.service
 * @date 2019/04/01 9:13
 */
public interface CartService {
    /**
    * @Date 2019/4/1 9:14
    * 添加商品到购物车
    * @Param [cartList 原来购物车列表, itemId skuId, num 购买数量]
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     * @return java.util.List<com.pinyougou.pojogroup.Cart>
     * @Date 2019/4/1 20:52
     * 从redis中查询购物车
     * @Param [username]
     **/
    public List<Cart> findCartListFromRedis(String username) ;

    /**
     * @return void
     * @Date 2019/4/1 20:53
     * 将购物车保存到redis
     * @Param [username, cartList]
     **/
    public  void saveCartListToRedis(String username, List<Cart> cartList);

    /**
    * @Date 2019/4/1 21:18
    * 购物车合并cookie及redis
    * @Param [cartList1, cartList2]
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
