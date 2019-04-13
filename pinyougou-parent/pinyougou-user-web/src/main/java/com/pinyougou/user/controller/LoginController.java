package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.user.controller
 * @date 2019/03/31 16:52
 */
@RestController
@RequestMapping(value = "/login")
public class LoginController {
    //得到登陆人账号
    @RequestMapping("name")
    public Map showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap<>();
        map.put("loginName", name);
        System.out.println(name+"---------------");
        return map;
    }

}
