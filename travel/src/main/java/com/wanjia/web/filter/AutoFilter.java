package com.wanjia.web.filter;

import com.wanjia.domain.User;
import com.wanjia.service.impl.UserService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AutoFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Cookie[] cookies = request.getCookies();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            if (cookies!=null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("auto")) {
                        String info = cookie.getValue();
                        String username = info.split("#")[0];
                        String password = info.split("#")[1];
                        UserService service = new UserService();
                        try {
                            user = service.checkLogin(username, password);
                        } catch (Exception e) {

                        }
                        session.setAttribute("user",user);
                    }
                }
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
