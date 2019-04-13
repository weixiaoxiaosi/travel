package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.cart.service.impl
 * @date 2019/04/01 9:15
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
    * @Date 2019/4/1 9:15
    * 购物车服务实现类
    * @Param [cartList, itemId, num]
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if(tbItem == null){
            throw  new RuntimeException("商品信息不存在!");
        }
        if(!"1".equals(tbItem.getStatus())){
            throw new RuntimeException("商品状态无效!");
        }
        //2.获取商家ID
        String sellerId = tbItem.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = this.seachCartBySellerId(cartList, sellerId);

        if (cart==null){
            //4.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            //新建此商家购物商列表对象
            //4.如果购物车列表中不存在该商家的购物车
            TbOrderItem orderItem1 =this.createOrderItem(tbItem, num);
            TbOrderItem orderItem = orderItem1;
            //4.2 将新建的购物车对象添加到购物车列表
            List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }
        //5.如果购物车列表中存在该商家的购物车
        else {
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = this.searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //5.1. 如果没有，新增购物车明细
            if (orderItem==null){
                orderItem = this.createOrderItem(tbItem, num);
                cart.getOrderItemList().add(orderItem);
            }
            //5.2. 如果有，在原购物车明细上添加数量，更改金额
            else {
                orderItem.setNum(orderItem.getNum()+num);
                System.out.println(orderItem.getNum()+"=============");
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0，则移除
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);//移除购物车明细
                    //如果移除后cart的明细数量为0，则将cart移除
                    if (cart.getOrderItemList().size()==0){
                        cartList.remove(cart);
                    }
                }
            }
        }

        return cartList;
    }

    /**
    * @Date 2019/4/1 20:57
    * 从redis中查询购物车
    * @Param [username]
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
    * @Date 2019/4/1 20:57
    * 将购物车存入redis
    * @Param [username, cartList]
    * @return void
    **/
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList );

    }

    /**
    * @Date 2019/4/1 21:18
    * 购物车合并
    * @Param [cartList1, cartList2]
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                this.addGoodsToCartList(cartList2,orderItem.getItemId() ,orderItem.getNum() );
            }
        }
        return cartList2;
    }


    /**
    * @Date 2019/4/1 9:50
    * 购物车明细列表中是否存在该商品
    * @Param [orderItemList, itemId]
    * @return com.pinyougou.pojo.TbOrderItem
    **/
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }

    /**
    * @Date 2019/4/1 9:43
    * 创建订单明细
    * @Param [tbItem, num]
    * @return com.pinyougou.pojo.TbOrderItem
    **/
    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setItemId(tbItem.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
        return orderItem;
    }

    /**
    * @Date 2019/4/1 9:37
    * 商家ID判断购物车列表中是否存在
    * @Param [cartList, sellerId]
    * @return com.pinyougou.pojogroup.Cart
    **/
    private Cart seachCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }


}
