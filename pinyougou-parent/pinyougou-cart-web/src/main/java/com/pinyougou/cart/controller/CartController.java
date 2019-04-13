package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.cart.controller
 * @date 2019/04/01 9:58
 */
@RestController
@RequestMapping(value ="/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
    * @Date 2019/4/1 10:15
    * 购物车列表
    * @Param []
    * @return java.util.List<com.pinyougou.pojogroup.Cart>
    **/
    @RequestMapping(value = "/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //从Cookie查询
        List<Cart> carts = new ArrayList<>();
        String cookieValue = CookieUtil.getCookieValue(request, "cartList", true);
        //如果购物车cookies有值
        if (StringUtils.isNotBlank(cookieValue)){
            //把json串转成list
            carts = JSON.parseArray(cookieValue, Cart.class);
        }
        if ("anonymousUser".equals(username)){
            return carts;
        }else {
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (carts.size()==0){
                return cartListFromRedis;
            }
            List<Cart> cartList = cartService.mergeCartList(carts, cartListFromRedis);
            //更新redis
            cartService.saveCartListToRedis(username,cartList );
            //删除cookie
            CookieUtil.deleteCookie(request,response , "cartList");
            return cartList;
        }

    }

    /**
    * @Date 2019/4/1 10:24
    * 添加购物车
    * @Param [itemId, num]
    * @return com.pinyougou.entity.Result
    **/
    @RequestMapping(value = "/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8087",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num){
        try {
            //设置可以访问的域，值设置为*时，允许所有域
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:8087");
            //如果需要操作cookies，必须加上此配置，标识服务端可以写cookies，
            //并且Access-Control-Allow-Origin不能设置为*，因为cookies操作需要域名
            //response.setHeader("Access-Control-Allow-Credentials", "true");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = this.findCartList();
            //注意，这里要重新接收一下，这里是调用接口查询数据
            cartList= cartService.addGoodsToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)){
                //存一天
                CookieUtil.setCookie(request, response,"cartList" ,JSON.toJSONString(cartList),3600*24,true );
            }else {
                cartService.saveCartListToRedis(username,cartList );
            }
            return new Result(true, "添加购物车成功");
        }catch (RuntimeException e){
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败");
        }
    }


}
