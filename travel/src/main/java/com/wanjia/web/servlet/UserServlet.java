package com.wanjia.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanjia.constant.Constant;
import com.wanjia.domain.ResultInfo;
import com.wanjia.domain.User;
import com.wanjia.factory.BeanFactory;
import com.wanjia.service.IUserService;
import com.wanjia.utils.Md5Util;
import com.wanjia.utils.UuidUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user")
public class UserServlet extends BeanServlet {
    private IUserService service = (IUserService) BeanFactory.getBean("userService");
    //校验用户名可用方法
    private ResultInfo checkUsername(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        ResultInfo info = null;
        try {
            User user = service.findByUser(username);
            info = new ResultInfo(true);
            if (user==null){
                info.setData(true);
                info.setErrorMsg("用户名可用");
            }else {
                info.setData(false);
                info.setErrorMsg("用户名已注册");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    //用户名注册方法
    private ResultInfo register (HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        ResultInfo info = new ResultInfo(true);
        try {
            BeanUtils.populate(user, map);
            user.setStatus(Constant.UNACTIVE);
            user.setCode(UuidUtil.getUuid());
            user.setPassword(Md5Util.encodeByMd5(user.getPassword()));
            System.out.println(user);
            boolean falg = service.saveUser(user);
            info.setData(falg);
            System.out.println(falg);
        } catch (Exception e) {
            info.setFlag(false);
            info.setErrorMsg(e.getMessage());
        }
       return info;
    }
    //用户名注册激活方法
    private ResultInfo active (HttpServletRequest request, HttpServletResponse response)  {
        String code = request.getParameter("code");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            User user =service.findUserByCode(code);
            if (user!=null){
                String status = user.getStatus();
                if (status.equals(Constant.UNACTIVE)){
                    user.setStatus(Constant.ACTIVED);
                    boolean flag = service.updateUser(user);
                    if (flag){
                        response.sendRedirect("login.html");
                    }
                }else {
                    //说明已激活，则不要重复激活
                    writer.write("请勿重复激活");
                }
            }else {
                //说明激活码错误，被篡改了
                writer.write("激活码被篡改了，激活失败");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return null;
    }
    //登录用户名校验
    private ResultInfo login (HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter("check");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String auto = request.getParameter("auto");
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute(Constant.CHECKCODE);
        ResultInfo info = new ResultInfo(true);
        if (checkCode.equalsIgnoreCase(code)) {
            //验证码正确
            try {
                User user = service.checkLogin(username,password);
                session.setAttribute("user",user );
                info.setData(true);
                if ("on".equals(auto)) {
                    //将username和password存放到cookie中
                    Cookie cookie = new Cookie("auto",username+"#"+password);
                    cookie.setMaxAge(7*24*60*60);
                    cookie.setPath(request.getContextPath());//设置cookie的作用范围是当前项目
                    response.addCookie(cookie);
                }
            } catch (Exception e) {
                info.setData(false);
                info.setErrorMsg(e.getMessage());
            }
        }else {
            //验证码错误
            info.setData(false);
            info.setErrorMsg("验证码错误");
        }
        return info;
    }
    //获取用户信息
    private ResultInfo getByUser (HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        ResultInfo info = new ResultInfo(true);
        info.setData(user);
        return info;
    }
    //退出登录
    private ResultInfo loginOut (HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        session.invalidate();
        try {
            response.sendRedirect("login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
