package com.wanjia.web.servlet;

import com.wanjia.domain.Favorite;
import com.wanjia.domain.ResultInfo;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IFavoriteService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/favorite")
public class FavoriteServlet extends BeanServlet {
    private IFavoriteService service = (IFavoriteService) BeanFactory.getBean("favoriteService");

    //是否已收藏
    private ResultInfo isFavorite(HttpServletRequest request , HttpServletResponse response){
        ResultInfo info = new ResultInfo();
        String rid = request.getParameter("rid");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user!=null){
            Favorite favorite = null;
            try {
                favorite = service.findFavorite(rid, user);
                if (favorite!=null){
                    info.setData(true);
                }else {
                    info.setData(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                info.setFlag(false);
                info.setErrorMsg(e.getMessage());
            }

        }else {
            info.setData(false);
        }
        return info;
    }
    //点击收藏
    private ResultInfo addFavorite(HttpServletRequest request , HttpServletResponse response){
        String rid = request.getParameter("rid");
        ResultInfo info = new ResultInfo(true);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user!=null){
            Integer count = service.findAddFavorite(rid, user);
            info.setData(count);
        }else {
            info.setData(false);
        }

        return info;
    }
    //取消收藏
    private ResultInfo cancelFavorite(HttpServletRequest request , HttpServletResponse response){
        ResultInfo info = new ResultInfo(true );
        String rid = request.getParameter("rid");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user!=null){
            Integer count = service.findByFavorite(rid, user);
            info.setData(count);
        }

        return  info;
    }
}
