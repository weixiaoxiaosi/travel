package com.pinyougou.content.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ContentController
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/20 21:30
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    @RequestMapping(value = "/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        List<TbContent> tbContents = contentService.findByCategoryId(categoryId);
        return tbContents;
    }

}
