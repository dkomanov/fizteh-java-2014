package ru.fizteh.fivt.students.standy66_new.server.tdb;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public interface TransactionDb {
    Transaction beginTransaction(String tableName);
}
