package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ItemSearchService
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/21 21:37
 * @Version 1.0
 **/
public interface ItemSearchService {

    /**
    * @Date 2019/3/25 16:34
    * 搜索功能
    * @Param [searchMap]
    * @return java.util.Map
    **/
    public Map search(Map searchMap);

    /**
     * 批量导入数据
     * @param list
     */
    public void importList(List list);

    /**
    * @Date 2019/3/26 20:06
    * 跟据id列表删除索引
    * @Param [goodsIdList]
    * @return void
    **/
    public void deleteByGoodsIds(Long[] ids);
}
