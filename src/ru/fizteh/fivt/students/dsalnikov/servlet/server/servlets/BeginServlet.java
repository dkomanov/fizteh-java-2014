package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BeginServlet extends AbstractHttpServletWrapper {

    public BeginServlet(TransactionManager manager) {
        super(manager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = getTableName(req, resp);
        String transactionId = transactionManager.beginTransaction(tableName);
        setUpResponse(resp);
        resp.getWriter().println(String.format("%s=%s", Paths.TRANSACTION_ID, transactionId));
    }
}
