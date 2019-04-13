package com.wanjia.exception;

/**
 * 用户名不存在异常
 */
public class PasswordErrorException extends Exception {
    public PasswordErrorException(){}
    public PasswordErrorException(String msg){
        super(msg);
    }

}
