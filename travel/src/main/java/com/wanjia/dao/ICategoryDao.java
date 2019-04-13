package com.wanjia.dao;

import com.wanjia.domain.Category;

import java.util.List;

public interface ICategoryDao {
    List<Category> findCategory()throws Exception;
}
