package com.wanjia.exception;

/**
 * 用户名不能为空异常
 */
public class UserNameNotNullException extends Exception {
    public UserNameNotNullException(){}
    public UserNameNotNullException(String msg){
        super(msg);
    }
}
