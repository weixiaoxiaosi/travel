package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserDetailsServiceImpl
 * 扩展权限认证类
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/13 17:37
 * @Version 1.0
 **/

public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构造用户的角色列表
        System.out.println(username + "进入了loadUserByUsername....");

        List<GrantedAuthority> authorities= new ArrayList<>();
//        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
//        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        TbSeller seller = sellerService.findOne(username);

//        TbSeller seller = sellerService.findOne(username);
        System.out.println(seller + "=================");
        if (seller!=null && "1".equals(seller.getStatus())){

//            if(seller != null && "1".equals(seller.getStatus())){
            return new User(username,seller.getPassword() ,authorities );

//            return new User(username,seller.getPassword(),authorities);
        }else{
            return null;
        }
    }
}
