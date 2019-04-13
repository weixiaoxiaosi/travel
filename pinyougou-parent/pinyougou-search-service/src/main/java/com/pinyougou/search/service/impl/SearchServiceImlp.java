package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.SolrItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SearchServiceImlp
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/21 21:43
 * @Version 1.0
 **/

@Service(timeout = 5000)
public class SearchServiceImlp implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 组装查询条件
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {



        Map map = new HashMap();
        //1.按关键字查询（高亮显示）
        searchList(searchMap, map);
        //2.根据关键字查询商品分类
        searchCategoryList(searchMap,map);


        //3.跟据商品分类名称查询商品品牌与规格列表
        String category = searchMap.get("category") == null ?  "" : searchMap.get("category").toString();
        if (category.trim().length()>0){
            searchBrandAndSpecList(category,map);
        }else {
            List<String> categoryList = (List<String>) map.get("categoryList");
            if (categoryList.size()>0){
                searchBrandAndSpecList(categoryList.get(0), map);
            }
        }

        return map;
    }

    /**
    * @Date 2019/3/25 16:35
    * 批量导入数据
    * @Param [list]
    * @return void
    **/
    @Override
    public void importList(List list) {

        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
    * @Date 2019/3/26 20:07
    * 跟据id列表删除索引
    * @Param [goodsIdList]
    * @return void
    **/
    @Override
    public void deleteByGoodsIds(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(ids);

        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 跟据商品分类名称查询商品品牌与规格列表
     * @param category
     * @param map
     */
    private void searchBrandAndSpecList(String category, Map map) {
        //获取redis里面的itemCat存储的id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId!=null){
            //查询品牌列表
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //查询规格列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
    }

    /**
     * 根据关键字查询商品分类
     * @param searchMap
     * @param map
     */
    private void searchCategoryList(Map searchMap, Map map) {
        //创建储存category数据
        List<String> categoryList = new ArrayList<>();
        // 1.创建查询条件对象query = new SimpleQuery()
        SimpleQuery query = new SimpleQuery();
        // 2.复制之前的Criteria组装查询条件的代码
        //关键字空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        // 3.创建分组选项对象new GroupOptions().addGroupByField(域名)
        GroupOptions category = new GroupOptions().addGroupByField("item_category");
        // 4.设置分组对象query.setGroupOptions
        query.setGroupOptions(category);
        // 5.得到分组页对象page = solrTemplate.queryForGroupPage
        GroupPage<SolrItem> page = solrTemplate.queryForGroupPage(query, SolrItem.class);
        // 6.得到分组结果集groupResult = page.getGroupResult(域名)
        GroupResult<SolrItem> groupResult = page.getGroupResult("item_category");
        // 7.得到分组结果入口groupEntries = groupResult.getGroupEntries()
        Page<GroupEntry<SolrItem>> groupEntries = groupResult.getGroupEntries();
        // 8.得到分组入口集合content = groupEntries.getContent()
        List<GroupEntry<SolrItem>> content = groupEntries.getContent();
        // 9.遍历分组入口集合content.for(entry)，记录结果entry.getGroupValue()
        for (GroupEntry<SolrItem> entry : content) {
            categoryList.add(entry.getGroupValue());
        }
        map.put("categoryList",categoryList);
    }

    /**
     * 搜索查询方法
     * @param searchMap
     * @param map
     */
    private void searchList(Map searchMap, Map map) {

        /**
         * 显示高亮查询
         */
        // 1.调用solrTemplate.queryForHighlightPage(query,class)方法，高亮查询数据
        // 2.构建query高亮查询对象new SimpleHighlightQuery
        HighlightQuery query = new SimpleHighlightQuery();
        // 3.复制之前的Criteria组装查询条件的代码
        //3.1关键字条件
        String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords").toString();
        if (keywords.trim().length() > 0) {
            Criteria criteria = new Criteria("item_keywords").is(keywords);
            query.addCriteria(criteria);
        }
        //3.2 商品分类条件
        String category = searchMap.get("category") == null ? "" : searchMap.get("category").toString();
        if(category.trim().length() > 0){
            Criteria criteria = new Criteria("item_category").is(category);
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //3.3 品牌条件
        String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand").toString();
        if(brand.trim().length() > 0){
            Criteria criteria = new Criteria("item_brand").is(brand);
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //3.4 规格条件
        String spec = searchMap.get("spec") == null ? "" : searchMap.get("spec").toString();
        if(spec.trim().length() > 0){
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                Criteria criteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //3.5 价格条件
        String price = searchMap.get("price")== null ? "":searchMap.get("price").toString();
        if (price.trim().length()>0){
            String[] split = price.split("-");

            //Criteria criteria = new Criteria("item_price").between(split[0],split[1]);

            //方案二:大于小于
            //商品价格大于开始价格
            if (!"0".equals(split[0])){
                Criteria criteria = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }

            //商品价格小于等于结束价格
            if (!"*".equals(split[1])){
                Criteria criteria = new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }

        }

        //3.6 分页参数获取
        Integer pageNo=searchMap.get("pageNo")== null ? 1 : new Integer(searchMap.get("pageNo").toString());
        Integer pageSize=searchMap.get("pageSize")== null ? 10 : new Integer(searchMap.get("pageSize").toString());
        //起始行号
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //3.7排序

        String sortValue = (String) searchMap.get("sort");//接收ASC 和DESC  参数

        String sortField = (String) searchMap.get("sortField");//排序字段
        System.out.println("=========="+sortField+sortValue);
        if (sortValue != null && !sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
                System.out.println("================="+query + sort);
            }

        }

        // 4.调用query.setHighlightOptions()方法，构建高亮数据三步曲：new HighlightOptions().addField(高亮业务域)，.setSimpleP..(前缀)，.setSimpleP..(后缀)
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red;'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
        // 5.接收solrTemplate.queryForHighlightPage的返回数据，定义page变量
        System.out.println(query.toString()+"=======================");
        HighlightPage<SolrItem> page = solrTemplate.queryForHighlightPage(query, SolrItem.class);
        // 6.遍历解析page对象，page.getHighlighted().for，item = h.getEntity()，
        // item.setTitle(h.getHighlights().get(0).getSnipplets().get(0))，在设置高亮之前最好判断一下;
        for (HighlightEntry<SolrItem> highlightEntry : page.getHighlighted()) {
            SolrItem item = highlightEntry.getEntity();
            if (highlightEntry.getHighlights().size() > 0 &&
                    highlightEntry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }
        }




        // 7.在循环完成外map.put("rows", page.getContent())返回数据列表
        map.put("rows", page.getContent());


        map.put("totalPages", page.getTotalPages());//总记录数

        map.put("total", page.getTotalElements());
    }
}
