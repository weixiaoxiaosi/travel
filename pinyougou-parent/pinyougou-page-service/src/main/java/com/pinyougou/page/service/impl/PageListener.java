package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019/03/28 11:17
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) msg.getObject();
            for (Long goodsId : goodsIds) {
                boolean genItemHtml = itemPageService.genItemHtml(goodsId);
                System.out.println("生成商品 "+ goodsId + " 静态页：" + genItemHtml);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
