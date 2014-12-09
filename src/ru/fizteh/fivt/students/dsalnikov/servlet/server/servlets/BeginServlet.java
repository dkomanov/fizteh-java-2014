package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BeginServlet extends HttpServlet {
    private TransactionManager manager;

    public BeginServlet(TransactionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = req.getParameter(Paths.TABLE_NAME);
        if (tableName == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "incorrect name provided");
        }
        String transactionId = manager.beginTransaction(tableName);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");

        resp.getWriter().println(String.format("%s=%s", Paths.TRANSACTION_ID, transactionId));
    }
}
