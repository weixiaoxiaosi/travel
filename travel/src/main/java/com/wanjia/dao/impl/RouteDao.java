package com.wanjia.dao.impl;

import com.wanjia.dao.IRouteDao;
import com.wanjia.domain.*;
import com.wanjia.utils.JDBCUtil;
import com.wanjia.utils.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;

public class RouteDao implements IRouteDao {
    private JdbcTemplate template =new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public List<Route> popRoute() throws Exception {
        List<Route> popRoute = template.query("SELECT * FROM tab_route WHERE rflag= 1 ORDER BY COUNT DESC LIMIT 0,4 ", new BeanPropertyRowMapper<>(Route.class));
        return popRoute;
    }

    @Override
    public List<Route> newesRoute() throws Exception {
        List<Route> newesRoute = template.query("SELECT * FROM tab_route WHERE rflag= 1 ORDER BY rdate DESC LIMIT 0,4", new BeanPropertyRowMapper<>(Route.class));
        return newesRoute;
    }

    @Override
    public List<Route> themeRoute() throws Exception {
        List<Route> themeRoute = template.query("SELECT * FROM tab_route WHERE rflag= 1 ORDER BY isThemeTour DESC LIMIT 0,4", new BeanPropertyRowMapper<>(Route.class));
        return themeRoute;
    }

    @Override
    public Integer findTotalSize(String cid,Integer currentPage,String keyword) throws Exception {
        String sql = "select count(*) from tab_route WHERE rflag = 1";
        List<Object> al = new ArrayList();
        if (!StringUtil.isEmpty(cid)){
            sql+=" and cid = ?";
            al.add(cid);
        }
        if (!StringUtil.isEmpty(keyword)){
            sql+=" and rname like  ?";
            al.add("%"+keyword+"%");
        }
        Integer totalSize = template.queryForObject(sql, Integer.class,al.toArray());
        return totalSize;
    }

    @Override
    public List<Route> findPageList(String cid,String keyword, Integer currentPage, Integer pageSize) throws Exception {
        String sql = "select * from tab_route where rflag= 1";
        List<Object> al = new ArrayList();
        if (!StringUtil.isEmpty(cid)){
            sql+=" and cid = ?";
            al.add(cid);
        }
        if (!StringUtil.isEmpty(keyword)){
            sql+=" and rname like  ?";
            al.add("%"+keyword+"%");
        }

        sql+=" LIMIT ?,?";
        al.add((currentPage-1)*pageSize);
        al.add(pageSize);
        Object[] obj = al.toArray();
        List<Route> list = template.query(sql, new BeanPropertyRowMapper<>(Route.class), obj);
        return list;
    }

    @Override
    public Route findDetailRoute(String rid) {
        String sql = "SELECT * FROM tab_route r ,tab_seller s ,tab_category c WHERE s.sid =r.sid and c.cid = r.cid AND r.rid=?";
        Map<String, Object> map = null;
        try {
            map = template.queryForMap(sql, rid);
        } catch (DataAccessException e) {
        }
        Route route = new Route();
        Seller seller = new Seller();
        Category category = new Category();
        try {
            BeanUtils.populate(route,map);
            BeanUtils.populate(seller,map);
            BeanUtils.populate(category,map );
            route.setCategory(category);
            route.setSeller(seller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return route;
    }
    @Override
    public List<RouteImg> findDetailImgs(String rid) {
        String sql = "SELECT * FROM tab_route_img  WHERE rid=?";
        List<RouteImg> imgs = null;
        try {
            imgs = template.query(sql, new BeanPropertyRowMapper<>(RouteImg.class), rid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return imgs;
    }

    @Override
    public void updateRouteCount(String rid, JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.update("update tab_route set count = count+1 where rid=? ",rid);
    }

    @Override
    public Integer findByCount(String rid) {
        Integer count = template.queryForObject("select count from tab_route where rid =? ", Integer.class, rid);
        return count;
    }

    @Override
    public void updateCount(String rid, JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.update("update tab_route set count = count-1 where rid=? ",rid);
    }

    @Override
    public Integer findCount(String rid) {
        Integer count = template.queryForObject("select count from tab_route where rid =? ", Integer.class, rid);
        return count;
    }

}
