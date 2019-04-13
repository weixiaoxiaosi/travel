package com.wanjia.dao;

import com.wanjia.domain.Favorite;
import com.wanjia.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

public interface IFavoriteDao {
    Favorite findFavorite(String rid, User user);

    void findByFavorite(String rid, User user, JdbcTemplate jdbcTemplate)throws Exception;

    void findAddFavorite(String rid, User user, JdbcTemplate jdbcTemplate)throws Exception;

}
