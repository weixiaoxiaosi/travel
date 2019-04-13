package com.wanjia.dao.impl;

import com.wanjia.dao.IFavoriteDao;
import com.wanjia.domain.Favorite;
import com.wanjia.domain.User;
import com.wanjia.utils.JDBCUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

public class FavoriteDao implements IFavoriteDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public Favorite findFavorite(String rid, User user){
        String sql = "select * from tab_favorite where rid = ? and uid = ?";
        Favorite favorite = null;
        try {
            favorite = template.queryForObject(sql, new BeanPropertyRowMapper<>(Favorite.class), rid, user.getUid());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return favorite;
    }

    @Override
    public void findByFavorite(String rid, User user, JdbcTemplate jdbcTemplate) throws Exception{
        String sql = "delete from tab_favorite where rid=? and uid=?";
        template.update(sql,rid,user.getUid());
    }

    @Override
    public void findAddFavorite(String rid, User user, JdbcTemplate jdbcTemplate)throws Exception{
        String sql = "insert into tab_favorite values (?,?,?)";
        template.update(sql,rid,new Date(),user.getUid());
    }
}
