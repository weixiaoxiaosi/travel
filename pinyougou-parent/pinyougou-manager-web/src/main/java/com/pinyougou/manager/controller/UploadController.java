package com.pinyougou.manager.controller;

import com.pinyougou.entity.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName UploadController
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/16 16:10
 * @Version 1.0
 **/
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile file){
        try {
            //1、图片原来的名字
            String filename = file.getOriginalFilename();
            //2、获取后缀名，不带"."
            String substring = filename.substring(filename.lastIndexOf(".") + 1);
            //3、创建FastDFS客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            //4、上传文件到FastDFS
            String uploadFile = fastDFSClient.uploadFile(file.getBytes(),substring);
            //5、拼接文件url
            String url = FILE_SERVER_URL + uploadFile;
            //6、返回文件url
            System.out.println("================="+url+"============"+uploadFile+"=========="+substring);
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败" );
        }

    }
}
