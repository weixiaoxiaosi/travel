package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * 微信支付实现
 * @description com.pinyougou.pay.service.impl
 * @date 2019/04/03 20:00
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;
    /**
    * @Date 2019/4/3 20:00
    * 生成微信支付二维码
    * @Param [out_trade_no, total_fee]
    * @return java.util.Map
    **/
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map map = new HashMap();

        try {
            //1、包装微信接口需要的参数
            Map param = new HashMap();
            param.put("appid", appid);//公众账号ID
            //商户号
            param.put("mch_id", partner);
            //随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            param.put("nonce_str", nonce_str);
            //商品描述	body
            param.put("body","三台商城");
            //商户订单号	out_trade_no
            param.put("out_trade_no", out_trade_no);
            //标价金额	total_fee
            param.put("total_fee", total_fee);
            //终端IP	spbill_create_ip
            param.put("spbill_create_ip","127.0.0.1" );
            //通知地址	notify_url
            param.put("notify_url", notifyurl);
            //交易类型	trade_type  NATIVE
            param.put("trade_type","NATIVE");
            //2、生成xml，通过httpClient发送请求得到数据
            String xmlParam  = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数:" + xmlParam);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //发起请求
            httpClient.post();
            //3、解析结果
            String content = httpClient.getContent();
            System.out.println("微信返回结果：" + content);
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);
            map.put("code_url", stringMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
    /**
    * @Date 2019/4/3 21:06 
    * 查询支付状态
    * @Param [out_trade_no]
    * @return java.util.Map
    **/ 
    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1、包装微信接口需要的参数
            Map param = new HashMap();
            //公众账号ID
            param.put("appid", appid);
            //商户号
            param.put("mch_id", partner);
            //商户订单号
            param.put("out_trade_no", out_trade_no);
            //随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            param.put("nonce_str", nonce_str);
            //2、生成xml，通过httpClient发送请求得到数据
            String xmlParam  = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数:" + xmlParam);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //发起请求
            httpClient.post();
            //3、解析结果
            String content = httpClient.getContent();
            System.out.println("微信返回结果：" + content);
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);
            return stringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * @Date 2019/4/8 10:14
    * 关闭支付
    * @Param [out_trade_no]
    * @return java.util.Map
    **/
    @Override
    public Map closePay(String out_trade_no) {
        try {
            //1、包装微信接口需要的参数
            Map param = new HashMap();
            //公众账号ID
            param.put("appid", appid);
            //商户号
            param.put("mch_id", partner);
            //商户订单号
            param.put("out_trade_no", out_trade_no);
            //随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            param.put("nonce_str", nonce_str);
            //2、生成xml，通过httpClient发送请求得到数据
            String xmlParam  = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数:" + xmlParam);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //发起请求
            httpClient.post();
            //3、解析结果
            String content = httpClient.getContent();
            System.out.println("微信返回结果：" + content);
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);
            return stringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
