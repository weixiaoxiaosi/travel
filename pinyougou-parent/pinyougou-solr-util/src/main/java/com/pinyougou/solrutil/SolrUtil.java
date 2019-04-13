package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.entity.SolrItem;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SolrUtil
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/21 21:09
 * @Version 1.0
 **/
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    /**
    * @Date 21:12 2019/3/21
    * 导入商品信息
    * @Param []
    * @return void
    **/
    public void importItemData(){
        TbItem where = new TbItem();
        where.setStatus("1");
        List<TbItem> tbItems = itemMapper.select(where);
//        System.out.println(tbItems);
        List<SolrItem> solrItemList = new ArrayList<>();
        SolrItem solrItem = null;
        for (TbItem tbItem : tbItems) {
            solrItem = new SolrItem();

            //使用spring的BeanUtils深克隆对象
            BeanUtils.copyProperties(tbItem,solrItem);
            solrItem.setPrice(tbItem.getPrice().doubleValue());
            //将spec字中的json字符串转换为map
            Map map = JSON.parseObject(tbItem.getSpec());
            solrItem.setSpecMap(map);
            solrItemList.add(solrItem);
        }
        solrTemplate.saveBeans(solrItemList);
        solrTemplate.commit();

    }


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");

        SolrUtil solrUtils = context.getBean(SolrUtil.class);

        solrUtils.importItemData();
    }
}
