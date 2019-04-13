package com.wanjia.dao;

import com.wanjia.domain.User;

public interface IUserDao {
    User findByUser(String username) throws Exception;

    boolean saveUser(User user) throws  Exception;

    User findUserByCode(String code) throws Exception;

    boolean updateUser(User user) throws Exception;

    User findByPw(String pw)throws Exception;
}
