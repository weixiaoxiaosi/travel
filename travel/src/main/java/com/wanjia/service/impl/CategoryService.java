package com.wanjia.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanjia.dao.ICategoryDao;
import com.wanjia.domain.Category;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.ICategoryService;
import com.wanjia.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CategoryService implements ICategoryService {
    private ICategoryDao dao = (ICategoryDao) BeanFactory.getBean("categoryDao");
    @Override
    public String findCategory() throws Exception {
        Jedis jedis = JedisUtil.getJedis();
        String all_category = jedis.get("all_category");
        if (all_category==null) {
            List<Category> categories =  dao.findCategory();
            ObjectMapper objectMapper = new ObjectMapper();
            all_category = objectMapper.writeValueAsString(categories);
            jedis.set("all_category", all_category);
        }
        return all_category;
    }
}
