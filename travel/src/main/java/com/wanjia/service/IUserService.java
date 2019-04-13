package com.wanjia.service;

import com.wanjia.domain.User;

public interface IUserService {
    User findByUser(String username) throws Exception;

    boolean saveUser(User user) throws Exception;

    User findUserByCode(String code) throws  Exception;

    boolean updateUser(User user) throws Exception;

    User checkLogin(String username, String password) throws Exception;
}
