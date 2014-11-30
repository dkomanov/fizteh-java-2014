package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class BeginServlet extends BaseServlet {
    public BeginServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String tableName = req.getParameter("table");
        Transaction t = binder.getDb().beginTransaction(tableName);
        res.getWriter().write(String.valueOf(binder.putTransaction(t)));
    }
}
