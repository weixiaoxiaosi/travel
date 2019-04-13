/*
package com.pinyougou.manager.servlet;


import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;

*/
/**
 * @author shenliang
 * @version 1.0
 * @description com.pinyougou.manager.servlet
 * @date 2019/03/24 11:04
 *//*

public class InitData2MemServlet extends HttpServlet {

    private NodeManagerService nodeManagerService;

    @Override
    public void init() throws ServletException {
        super.init();


        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        nodeManagerService=(NodeManagerService)wac.getBean("nodeManagerService");
        nodeManagerService.getNode2Map(new HashMap<String,Object>(),SystemMem.nodeMap);
    }
}
*/
