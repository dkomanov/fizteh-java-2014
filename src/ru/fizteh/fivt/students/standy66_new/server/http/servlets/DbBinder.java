package ru.fizteh.fivt.students.standy66_new.server.http.servlets;

import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.server.tdb.TransactionDb;

/**
 * @author andrew
 *         Created by andrew on 01.12.14.
 */
public interface DbBinder {
    TransactionDb getDb();
    int putTransaction(Transaction transaction);
    Transaction getTransaction(int id);
}
