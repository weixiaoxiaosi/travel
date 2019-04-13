package com.wanjia.exception;

/**
 * 用户名不存在异常
 */
public class UserNameNotExistsException extends Exception {
    public UserNameNotExistsException(){}
    public UserNameNotExistsException(String msg){
        super(msg);
    }

}
