package ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets;

import ru.fizteh.fivt.students.dsalnikov.servlet.database.Transaction;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractHttpServletWrapper extends HttpServlet {
    protected TransactionManager transactionManager;

    public AbstractHttpServletWrapper(TransactionManager manager) {
        this.transactionManager = manager;
    }

    protected String getTableName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tableName = request.getParameter(Paths.TABLE_NAME);
        if (tableName == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "incorrectNameProvided");
            return null;
        }
        return tableName;
    }

    protected void setUpResponse(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
    }

    protected Transaction getTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String transationId = getTransactionId(request, response);
        Transaction transaction = transactionManager.getTransaction(transationId);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transation not found");
            return null;
        }
        return transaction;
    }

    protected String getTransactionId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String transactionId = request.getParameter(Paths.TRANSACTION_ID);
        if (transactionId == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        return transactionId;
    }
}
