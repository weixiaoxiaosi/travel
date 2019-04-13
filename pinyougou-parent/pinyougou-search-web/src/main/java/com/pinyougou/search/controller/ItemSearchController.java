package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @ClassName ItemSearchController
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/22 9:33
 * @Version 1.0
 **/

@RestController
@RequestMapping(value = "/search")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping(value = "/find")
    public Map search(@RequestBody Map searchMap){
        return itemSearchService.search(searchMap);
    }
}
