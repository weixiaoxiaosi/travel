package com.pinyougou.page.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.File;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019/03/28 16:07
 */
@Component
public class PageDeleteListener implements MessageListener {
    @Value("${PAGE-DIR}")
    private  String PAGEDIR;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) msg.getObject();
            for (Long goodsId : goodsIds) {
                File file = new File(PAGEDIR+goodsId+".html");
                boolean delete = file.delete();
                System.out.println("删除商品 "+ goodsId + " 静态页：" + delete);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
