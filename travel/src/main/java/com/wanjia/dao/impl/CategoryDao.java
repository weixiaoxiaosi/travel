package com.wanjia.dao.impl;

import com.wanjia.dao.ICategoryDao;
import com.wanjia.domain.Category;
import com.wanjia.utils.JDBCUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CategoryDao implements ICategoryDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public List<Category> findCategory() throws Exception {
        List<Category> categories = template.query("select * from tab_categoury", new BeanPropertyRowMapper<>(Category.class));
        return categories;
    }
}
