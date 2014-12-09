package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.Transaction;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SizeServlet extends HttpServlet {
    TransactionManager manager;

    public SizeServlet(TransactionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String transactionId = req.getParameter(Paths.TRANSACTION_ID);
        if (transactionId == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error getting transactionID");
            return;
        }

        Transaction transaction = manager.getTransaction(transactionId);
        if (transaction == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error getting transaction");
            return;
        }

        int result = transaction.size();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");

        resp.getWriter().println(result);
    }
}
