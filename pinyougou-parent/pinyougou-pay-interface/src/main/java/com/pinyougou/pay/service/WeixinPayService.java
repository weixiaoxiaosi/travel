package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * 微信支付接口
 * @description com.pinyougou.pay.service
 * @date 2019/04/03 19:58
 */
public interface WeixinPayService {

    /**
    * @Date 2019/4/3 19:59
    * 生成微信支付二维码
    * @Param [out_trade_no 订单号, total_fee 金额(分)]
    * @return java.util.Map
    **/
    public Map createNative(String out_trade_no, String total_fee);

    /**
    * @Date 2019/4/3 21:06
    * 查询支付状态
    * @Param [out_trade_no 商户订单号]
    * @return java.util.Map
    **/ 
    public Map queryPayStatus(String out_trade_no);

    /**
    * @Date 2019/4/8 10:14
    * 关闭支付
    * @Param [out_trade_no]
    * @return java.util.Map
    **/
    public  Map closePay(String out_trade_no);

}
