package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 01.12.14.
 */
public abstract class BaseDbServlet extends HttpServlet {
    protected DbBinder binder;

    public BaseDbServlet(DbBinder binder) {
        this.binder = binder;
    }

    protected final Transaction getTransaction(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int transactionId = Integer.parseInt(req.getParameter("tid"));
        return binder.getTransaction(transactionId);
    }

    protected final void sendErrorNoTransaction(HttpServletResponse res) throws IOException {
        res.sendError(500, "No transaction");
    }

    protected final void sendErrorNoTable(HttpServletResponse res) throws IOException {
        res.sendError(500, "No table selected");
    }

    protected final void sendErrorInternalError(HttpServletResponse res) throws IOException {
        res.sendError(500, "Internal error");
    }

}

