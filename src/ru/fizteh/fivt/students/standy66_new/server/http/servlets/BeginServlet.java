package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class BeginServlet extends BaseDbServlet {
    public BeginServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = req.getParameter("table");
        if (tableName == null) {
            sendErrorNoTable(resp);
        } else {
            int transactionId = binder.beginTransaction(tableName);
            if (binder.getTransaction(transactionId) != null) {
                resp.setStatus(200);
                resp.getWriter().write("OK! Transaction id: " + transactionId);
            } else {
                sendErrorInternalError(resp);
            }
        }
    }
}
