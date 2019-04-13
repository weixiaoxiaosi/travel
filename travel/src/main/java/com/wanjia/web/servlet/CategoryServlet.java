package com.wanjia.web.servlet;

import com.wanjia.domain.Category;
import com.wanjia.domain.ResultInfo;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.ICategoryService;
import com.wanjia.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/category")
public class CategoryServlet extends BeanServlet {
    private ICategoryService service = (ICategoryService) BeanFactory.getBean("categoryService");

    //导航栏填充
    private ResultInfo findAll(HttpServletRequest request ,HttpServletResponse response){
        ResultInfo info = new ResultInfo(true);
        try {
            String category = service.findCategory();
            List<Category> json = JsonUtil.readJson(category, List.class, Category.class);
            info.setData(json);
        } catch (Exception e) {
            info.setFlag(false);
            info.setErrorMsg(e.getMessage());
        }
        return info;
    }
}
