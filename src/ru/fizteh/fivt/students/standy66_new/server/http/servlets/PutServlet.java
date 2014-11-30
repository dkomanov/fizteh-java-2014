package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;

import javax.servlet.*;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class PutServlet extends BaseServlet {
    public PutServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Transaction transaction = getTransaction(req, res);
        if (transaction != null) {
            try {
                transaction.put(req.getParameter("key"), TableRow.deserialize(transaction.getSignature(), req.getParameter("value")));
            } catch (ParseException e) {
                res.getWriter().write("parse exception");
            }

        }
    }
}
