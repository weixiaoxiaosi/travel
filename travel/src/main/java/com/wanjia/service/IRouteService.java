package com.wanjia.service;

import com.wanjia.domain.PageRoute;
import com.wanjia.domain.Route;
import com.wanjia.domain.User;

import java.util.List;
import java.util.Map;

public interface IRouteService {
    Map<String,List<Route>> findByRoute()throws Exception;

    PageRoute<Route> findByPage(String cid, Integer currentPage, String keyword)throws Exception;

    Route findDetailRoute(String rid)throws Exception;

}
