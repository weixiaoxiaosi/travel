package com.wanjia.exception;

/**
 * 用户名已存在异常
 */
public class UserNameExistsException extends Exception {
    public UserNameExistsException(){}
    public UserNameExistsException(String msg){
        super(msg);
    }

}
