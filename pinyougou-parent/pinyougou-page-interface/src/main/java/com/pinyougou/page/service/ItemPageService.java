package com.pinyougou.page.service;

/**
 * @author shenliang
 * @version 1.0
 * 商品详细页接口
 * @description com.pinyougou.page.service
 * @date 2019/03/26 20:30
 */
public interface ItemPageService {

    /**
    * @Date 2019/3/26 20:50
    * 生成商品详细页
    * @Param [goodsId]
    * @return boolean
    **/
    public boolean genItemHtml (Long goodsId);
}
