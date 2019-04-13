package com.wanjia.service.impl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wanjia.constant.Constant;
import com.wanjia.dao.IFavoriteDao;
import com.wanjia.dao.IRouteDao;
import com.wanjia.domain.PageRoute;
import com.wanjia.domain.Route;
import com.wanjia.domain.RouteImg;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IRouteService;
import com.wanjia.utils.JDBCUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteService implements IRouteService {
    private IRouteDao dao = (IRouteDao) BeanFactory.getBean("routeDao");
    @Override
    public Map<String, List<Route>> findByRoute() throws Exception {
        Map<String, List<Route>> map = new HashMap<>();
        List<Route> popRoute = dao.popRoute();
        List<Route> newesRoute = dao.newesRoute();
        List<Route> themeRoute = dao.themeRoute();
        map.put("popRoute", popRoute);
        map.put("newesRoute", newesRoute);
        map.put("themeRoute", themeRoute);
        return map;
    }

    @Override
    public PageRoute<Route> findByPage(String cid, Integer currentPage, String keyword) throws Exception {
        PageRoute<Route> pageRoute = new PageRoute<>();
        //每页显示条数
        pageRoute.setPageSize(Constant.PAGESIZE);
        Integer pageSize = pageRoute.getPageSize();
        //当前页数
        pageRoute.setCurrentPage(currentPage);
        //总条数
        Integer totalSize = dao.findTotalSize(cid,currentPage,keyword);
        pageRoute.setTotalSize(totalSize);
        //总页数
        Integer totalPage = pageRoute.getTotalPage();
        //每页显示数据
        List<Route> list = dao.findPageList(cid,keyword,currentPage,pageSize);
        pageRoute.setList(list);
        return pageRoute;
    }

    @Override
    public Route findDetailRoute(String rid) throws Exception {
        Route route = dao.findDetailRoute(rid);
        List<RouteImg> list= dao.findDetailImgs(rid);
        route.setRouteImgList(list);
        return route;
    }


}
