package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class GetServlet extends BaseServlet {
    public GetServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Transaction transaction = getTransaction(req, res);
        if (transaction != null) {
            res.getWriter().write(transaction.get(req.getParameter("key")).serialize());
        }
    }
}
