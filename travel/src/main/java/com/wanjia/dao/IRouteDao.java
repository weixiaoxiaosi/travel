package com.wanjia.dao;

import com.wanjia.domain.Route;
import com.wanjia.domain.RouteImg;
import com.wanjia.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface IRouteDao {
    List<Route> popRoute()throws Exception;

    List<Route> newesRoute()throws Exception;

    List<Route> themeRoute()throws Exception;

    Integer findTotalSize(String cid,Integer currentPage,String keyword)throws Exception;

    List<Route> findPageList(String cid,String keyword, Integer currentPage, Integer pageSize)throws Exception;

    Route findDetailRoute(String rid);

    List<RouteImg> findDetailImgs(String rid);

    void updateRouteCount(String rid, JdbcTemplate jdbcTemplate)throws Exception;

    Integer findByCount(String rid);


    void updateCount(String rid, JdbcTemplate jdbcTemplate)throws Exception;

    Integer findCount(String rid);
}
