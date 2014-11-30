package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 01.12.14.
 */
public abstract class BaseServlet implements Servlet {
    protected ServletConfig config;
    protected DbBinder binder;

    protected Transaction getTransaction(ServletRequest req, ServletResponse res) throws IOException {
        int transactionId = Integer.parseInt(req.getParameter("tid"));
        Transaction transaction = binder.getTransaction(transactionId);
        if (transaction == null) {
            res.getWriter().write("No transaction");
        }
        return transaction;
    }

    public BaseServlet(DbBinder binder) {
        this.binder = binder;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void destroy() {

    }

    @Override
    public String getServletInfo() {
        return "Abstract Servlet";
    }
}
