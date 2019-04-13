package com.wanjia.service.impl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wanjia.dao.IFavoriteDao;
import com.wanjia.dao.IRouteDao;
import com.wanjia.domain.Favorite;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IFavoriteService;
import com.wanjia.utils.JDBCUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class FavoriteService implements IFavoriteService {
    private IFavoriteDao dao = (IFavoriteDao) BeanFactory.getBean("favoriteDao");
    @Override
    public Integer findAddFavorite(String rid, User user) {
        //获取数据源
        DataSource dataSource = JDBCUtil.getDataSource();
        //获取一个数据连接
        Connection connection = null ;
        IRouteDao routeDao = (IRouteDao) BeanFactory.getBean("routeDao");
        Integer count = 0 ;
        try {
            connection = dataSource.getConnection();
            //开启事务管理器
            TransactionSynchronizationManager.initSynchronization();
            //保证开启事务与执行语句一样
            JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
            //修改事务默认属性
            connection.setAutoCommit(false);
            dao.findAddFavorite(rid,user,jdbcTemplate);
            routeDao.updateRouteCount(rid,jdbcTemplate);
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
                TransactionSynchronizationManager.clearSynchronization();
            } catch (SQLException e1) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
                count =routeDao.findByCount(rid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Override
    public Integer findByFavorite(String rid, User user) {
        //获取连接数据
        DataSource dataSource = JDBCUtil.getDataSource();
        Connection connection = null;
        IRouteDao routeDao = (IRouteDao) BeanFactory.getBean("routeDao");
        Integer count = 0;
        try {
            connection = dataSource.getConnection();
            //开启事务管理器
            TransactionSynchronizationManager.initSynchronization();
            //保证开启事务与执行语句一样
            JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
            //修改事务默认属性
            connection.setAutoCommit(false);
            dao.findByFavorite(rid,user,jdbcTemplate);

            routeDao.updateCount(rid,jdbcTemplate);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                TransactionSynchronizationManager.clearSynchronization();
            } catch (SQLException e1) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
                count =routeDao.findByCount(rid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Override
    public Favorite findFavorite(String rid, User user) throws Exception{
        Favorite favorite = dao.findFavorite(rid, user);
        return favorite;
    }
}
