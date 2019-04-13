package com.wanjia.web.servlet;

import com.wanjia.domain.PageRoute;
import com.wanjia.domain.ResultInfo;
import com.wanjia.domain.Route;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IRouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/route")
public class RouteServlet extends BeanServlet {
    private IRouteService service = (IRouteService) BeanFactory.getBean("routeService");
    //黑马精选方法
    private ResultInfo findJingXuan(HttpServletRequest request,HttpServletResponse response){
        ResultInfo info = new ResultInfo(true);
        Map<String , List<Route>> map = null;
        try {
            map = service.findByRoute();
            System.out.println(map);
            info.setData(map);
        } catch (Exception e) {
            info.setFlag(false);
            info.setErrorMsg(e.getMessage());
        }
        return info;
    }
    //搜索查询分页方法
    private ResultInfo findPage(HttpServletRequest request,HttpServletResponse response){
        String cid = request.getParameter("cid");
        Integer currentPage = Integer.valueOf(request.getParameter("currentPage"));
        String keyword = request.getParameter("keyword");
        ResultInfo info = new ResultInfo(true);
        PageRoute<Route> pageRoute = null;
        try {
            pageRoute = service.findByPage(cid,currentPage,keyword);
            info.setData(pageRoute);
        } catch (Exception e) {
            info.setFlag(false);
            info.setErrorMsg(e.getMessage());
        }
        return info;
    }
    //查看详情页面方法
    private ResultInfo detailRoute (HttpServletRequest request,HttpServletResponse response){
        ResultInfo info = new ResultInfo(true);
        String rid = request.getParameter("rid");
        Route route = null;
        try {
            route = service.findDetailRoute(rid);
            info.setData(route);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg(e.getMessage());
        }
        return info;
    }

}
