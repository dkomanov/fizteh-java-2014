package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.Transaction;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Dmitriy on 12/2/2014.
 */
public class PutServlet extends HttpServlet {

    private TransactionManager manager;

    public PutServlet(TransactionManager manager) {
        this.manager = manager;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String transactionId = req.getParameter(Paths.TRANSACTION_ID);
        if (transactionId == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in transaction ID");
            return;
        }

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

        Transaction transaction = manager.getTransaction(transactionId);

        try {
            String previousValue = transaction.put(key, value);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF8");

            resp.getWriter().println(previousValue);
        } catch (IllegalArgumentException iae) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.getMessage());
        }

    }
}
