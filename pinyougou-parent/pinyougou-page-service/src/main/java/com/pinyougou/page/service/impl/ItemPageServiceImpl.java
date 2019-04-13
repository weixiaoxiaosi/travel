package com.pinyougou.page.service.impl;

import com.github.abel533.entity.Example;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019/03/26 20:48
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper descMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Value("${PAGE-DIR}")
    private String PAGEDIR;
    /**
    * @Date 2019/3/26 20:50
    * 生成商品详细页
    * @Param [goodsId]
    * @return boolean
    **/
    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            //读取模板对象
            Configuration cfg = freeMarkerConfigurer.getConfiguration();
            Template template = cfg.getTemplate("item.ftl");
            //构建数据模型对象
            Map dateModel = new HashMap();
            //1.查询商品基本信息
            TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            System.out.println(goods+"============");
            dateModel.put("goods",goods );
            //2.查询商品描述信息
            TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(goodsId);
            dateModel.put("goodsDesc",goodsDesc );
            //3.查询商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dateModel.put("itemCat1", itemCat1);
            dateModel.put("itemCat2", itemCat2);
            dateModel.put("itemCat3", itemCat3);
            //4.查询SKU信息
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId", goodsId);
            criteria.andEqualTo("status", 1);
            //按默认降序，为了第一个选中默认的
            example.setOrderByClause("isDefault desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dateModel.put("itemList",itemList );

            //输出静态html
            Writer out = new FileWriter(PAGEDIR+goodsId+".html");
            template.process(dateModel,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
