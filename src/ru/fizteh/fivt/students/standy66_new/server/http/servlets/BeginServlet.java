package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class BeginServlet implements Servlet {
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        res.getWriter().write(req.toString());
    }

    @Override
    public String getServletInfo() {
        return "A simple servlet";
    }

    @Override
    public void destroy() {

    }
}
