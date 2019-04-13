package com.wanjia.web.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanjia.domain.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            Class<? extends BeanServlet> aClass = this.getClass();
            Method method = aClass.getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            ResultInfo info = (ResultInfo) method.invoke(this, request, response);
            if (info!=null){
                ObjectMapper objectMapper = new ObjectMapper();
                PrintWriter writer = null;
                try {
                    String json = objectMapper.writeValueAsString(info);
                    writer = response.getWriter();
                    writer.write(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    writer.close();
                }
            }
        } catch (Exception e) {

        }
    }
}
