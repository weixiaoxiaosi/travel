package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * 支付控制层
 * @description com.pinyougou.cart.controller
 * @date 2019/04/03 20:20
 */
@RestController
@RequestMapping(value = "/pay")
public class PayController {

    @Reference(timeout = 7000)
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;
    /**
    * @Date 2019/4/3 20:22
    * 生成二维码
    * @Param []
    * @return java.util.Map
    **/
    @RequestMapping(value = "/createNative")
    public Map createNative(){

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);

        if (seckillOrder!=null){
            String fen =(long) (seckillOrder.getMoney().doubleValue() * 100) + "";
            return weixinPayService.createNative(seckillOrder.getId()+"",fen );
        }else {
            return new HashMap();
        }
    }

    /**
    * @Date 2019/4/4 15:52
    * 查询支付状态
    * @Param [out_trade_no]
    * @return com.pinyougou.entity.Result
    **/
    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result= null;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x=0;
        while (true){
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
            System.out.println(map.toString());
            if (map==null){//支付出错
                result= new Result(false,"支付出错" );
                break;
            }if (map.get("trade_state").equals("SUCCESS")){//支付成功
                result= new Result(true,"支付成功" );
                //创建抢购订单到数据库
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
            x++;
            if (x>=100){
                result=new  Result(false, "二维码超时");
                //清除缓存
                //seckillOrderService.deleteOrderFromRedis(userId,new Long(out_trade_no));
                Map<String,String> closePay = weixinPayService.closePay(out_trade_no);
                //如果返回结果是正常关闭
                if (!"SUCCESS".equals(closePay.get("result_code"))){
                    if ("ORDERPAID".equals(closePay.get("err_code"))){
                        result=new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                if (result.isSuccess()==false){
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                    result=new  Result(false, "订单已超时!!!");
                }
                break;
            }
        }
        return result;
    }


}
