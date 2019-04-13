package com.wanjia.service.impl;

import com.wanjia.constant.Constant;
import com.wanjia.dao.IUserDao;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IUserService;
import com.wanjia.utils.MailUtil;
import com.wanjia.utils.Md5Util;

public class UserService implements IUserService {
    private IUserDao dao = (IUserDao) BeanFactory.getBean("userDao");
    @Override
    public User findByUser(String username) throws Exception {
        User user = dao.findByUser(username);
        return user;
    }

    @Override
    public boolean saveUser(User user) throws Exception {

        boolean flag = dao.saveUser(user);
        if (flag){
            MailUtil.sendMail(user.getEmail(), "欢迎注册黑马旅游网，请点击下面的按钮进行<a href='http://localhost:8080/travel1/user?action=active&code="+user.getCode()+"'>激活</a>");
        }
        return flag;
    }

    @Override
    public User findUserByCode(String code) throws Exception {
        User user = dao.findUserByCode(code);
        return user;
    }

    @Override
    public boolean updateUser(User user) throws Exception {
        boolean flag = dao.updateUser(user);
        return flag;
    }

    @Override
    public User checkLogin(String username, String password) throws Exception {
        User user = dao.findByUser(username);
        if (user!=null){
            String pw = Md5Util.encodeByMd5(password);

            if (user.getPassword().equals(pw)){
                if (user.getStatus().equals(Constant.ACTIVED)) {
                    return user;
                }else {
                    //未激活
                    throw new RuntimeException("账户未激活");
                }
            }else {
                //密码不正确
                throw new RuntimeException("密码错误");
            }
        }else {
            //用户名不正确
            throw new RuntimeException("用户名错误");
        }
    }
}
