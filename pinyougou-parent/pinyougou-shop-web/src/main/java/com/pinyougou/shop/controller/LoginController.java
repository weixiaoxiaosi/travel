package com.pinyougou.shop.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LoginController
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/13 14:53
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @RequestMapping(value = "/name")
    public Map login(){
        Map map = new HashMap();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", name);
        return map;
    }
}
