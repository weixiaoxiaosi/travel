package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.SolrItem;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.search.service.impl
 * @date 2019/03/27 21:25
 */
@Component
public class itemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            String json = msg.getText();
            List<TbItem> itemList = JSON.parseArray(json, TbItem.class);
            List<SolrItem> solrItems = new ArrayList<>();
            SolrItem solrItem = null;
            for (TbItem item : itemList) {
                solrItem = new SolrItem();
                //使用深克隆,把TbItem与SolrItem相同属性的，内容复制过来
                //copyProperties(数据源,复制目标)
                BeanUtils.copyProperties(item,solrItem);
                //转换规格数据
                Map specMap = JSON.parseObject(item.getSpec(), Map.class);
                solrItem.setSpecMap(specMap);
                solrItems.add(solrItem);
            }
            itemSearchService.importList(solrItems);
            System.out.println("成功导入索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
