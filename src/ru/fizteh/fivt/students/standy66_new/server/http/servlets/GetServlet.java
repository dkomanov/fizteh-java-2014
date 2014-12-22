package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class GetServlet extends BaseDbServlet {
    public GetServlet(DbBinder dbBinder) {
        super(dbBinder);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Transaction transaction = getTransaction(req, res);
        if (transaction != null) {
            String key = req.getParameter("key");
            if (key == null) {
                res.sendError(400, "no key specified");
            } else {
                TableRow value = transaction.get(key);
                if (value == null) {
                    res.sendError(404, "Not found");
                } else {
                    res.setStatus(200);
                    res.getWriter().write(value.serialize());
                }
            }
        } else {
            sendErrorNoTransaction(res);
        }
    }
}
