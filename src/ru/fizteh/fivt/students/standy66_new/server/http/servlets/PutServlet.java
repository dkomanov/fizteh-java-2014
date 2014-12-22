package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class PutServlet extends BaseDbServlet {
    public PutServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Transaction transaction = getTransaction(req, res);
        if (transaction != null) {
            String key = req.getParameter("key");
            String sValue = req.getParameter("value");
            if (key == null) {
                res.sendError(400, "no key specified");
            } else if (sValue == null) {
                res.sendError(400, "no value specified");
            } else {
                try {
                    TableRow newValue = TableRow.deserialize(transaction.getSignature(), sValue);
                    TableRow oldValue = transaction.put(key, newValue);
                    res.setStatus(200);
                    res.getWriter().write("OK. Old value: " + oldValue);
                } catch (ParseException e) {
                    res.sendError(400, "error while parsing");
                }
            }
        } else {
            sendErrorNoTransaction(res);
        }
    }
}
