package com.wanjia.dao.impl;

import com.wanjia.dao.IUserDao;
import com.wanjia.domain.User;
import com.wanjia.utils.JDBCUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao implements IUserDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public User findByUser(String username) throws Exception {
        User user = null;
        try {
            user = template.queryForObject("select * from tab_user where username = ?", new BeanPropertyRowMapper<>(User.class), username);
        } catch (DataAccessException e) {
        }
        return user;
    }

    @Override
    public boolean saveUser(User user) throws Exception {
        String sql = "insert into tab_user values(null,?,?,?,?,?,?,?,?,?)";
        boolean flag = false;
        try {
            int i = template.update(sql, user.getUsername(), user.getPassword(), user.getName(), user.getBirthday(), user.getSex(), user.getTelephone(), user.getEmail(), user.getStatus(), user.getCode());
            if (i == 1) {
                flag = true;
            }
        } catch (DataAccessException e) {
        }
        return flag;
    }

    @Override
    public User findUserByCode(String code) throws Exception {
        User user = null;
        try {
            user = template.queryForObject("select * from tab_user where code = ?", new BeanPropertyRowMapper<>(User.class), code);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean updateUser(User user) throws Exception {
        String sql = "update tab_user set username=?,password=?,name=?,birthday=?,telephone=?,email=?,status=? where uid=?";
        boolean flag = false;
        try {
            int update = template.update(sql, user.getUsername(), user.getPassword(), user.getName(), user.getBirthday(), user.getTelephone(), user.getEmail(), user.getStatus(), user.getUid());
            if (update == 1) {
                flag=true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public User findByPw(String pw) throws Exception {
        User user = null;
        try {
            user = template.queryForObject("select  from tab_user where password = ?", new BeanPropertyRowMapper<>(User.class), pw);
        } catch (DataAccessException e) {

        }
        return user;
    }
}
