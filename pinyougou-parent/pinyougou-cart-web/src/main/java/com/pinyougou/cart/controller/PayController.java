package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private OrderService orderService;
    /**
    * @Date 2019/4/3 20:22
    * 生成二维码
    * @Param []
    * @return java.util.Map
    **/
    @RequestMapping(value = "/createNative")
    public Map createNative(){
      /*  IdWorker idWorker = new IdWorker() ;
        return weixinPayService.createNative(idWorker.nextId()+"", "1");*/
        //整合日志功能后的代码
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        Map aNative = weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");

        return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
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
        int x=0;
        while (true){
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
            System.out.println(map.toString());
            if (map==null){//支付出错
                result= new Result(false,"支付出错" );
                break;
            }if (map.get("trade_state").equals("SUCCESS")){//支付成功
                result= new Result(true,"支付成功" );
                //更新订单状态与日志状态
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id").toString());
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
                break;
            }
        }
        return result;
    }


}
