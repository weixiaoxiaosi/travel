package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @author shenliang
 * @version 1.0
 * 接收jms数据并完成删除操作
 * @description com.pinyougou.search.service.impl
 * @date 2019/03/28 9:41
 */
@Component
public class itemSearchDeleteLintener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    /**
    * @Date 2019/3/28 9:45
    * 接收ids数据,完成删除操作
    * @Param [message]
    * @return void
    **/
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage) message;
            Long[] ids = (Long[]) msg.getObject();
            itemSearchService.deleteByGoodsIds(ids);
            System.out.println("成功删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
