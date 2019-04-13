package com.wanjia.utils;

/**
 * 包名:com.itheima.travel.utils
 * 作者:Leevi
 * 日期2019-01-18  17:47
 */
public class StringUtil {
    /**
     * 判断一个字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str == null || str.equals("") || str.equals("null")){
            return true;
        }else {
            return false;
        }   
    }
}
