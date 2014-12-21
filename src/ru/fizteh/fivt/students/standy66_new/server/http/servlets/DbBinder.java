package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;

/**
 * @author andrew
 *         Created by andrew on 01.12.14.
 */
public interface DbBinder {
    int beginTransaction(String tableName);
    Transaction getTransaction(int id);
}
