package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.Transaction;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PutServlet extends AbstractHttpServletWrapper {


    public PutServlet(TransactionManager manager) {
        super(manager);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String key = req.getParameter(Paths.KEY);
        if (key == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Key getting error");
            return;
        }
        String value = req.getParameter(Paths.VALUE);
        if (value == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting key");
            return;
        }

        Transaction transaction = getTransaction(req, resp);

        try {
            String previousValue = transaction.put(key, value);

            setUpResponse(resp);

            resp.getWriter().println(previousValue);
        } catch (IllegalArgumentException iae) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.getMessage());
        }

    }
}
