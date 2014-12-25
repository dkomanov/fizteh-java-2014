package ru.fizteh.fivt.students.dsalnikov.servlet.database;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTable;
import ru.fizteh.fivt.students.dsalnikov.storable.TransactionChanges;

import java.io.IOException;
import java.text.ParseException;

public class Transaction {

    TableProvider tableProvider;
    StorableTable table;
    TransactionChanges localChanges;
    TransactionManager manager;
    TransactionChanges defaultTrasaction;
    String transactionId;

    public Transaction(TableProvider tableProvider, String tableName, TransactionManager manager) {
        this.tableProvider = tableProvider;
        this.manager = manager;
        table = (StorableTable) tableProvider.getTable(tableName);
        if (table == null) {
            throw new IllegalArgumentException("no such table " + tableName);
        }
        defaultTrasaction = table.getTransaction();
        localChanges = new TransactionChanges();
        localChanges.setStorage(table);
        transactionId = manager.makeTransactionId();

    }

    public int commit() {
        table.setTransaction(localChanges);
        int result = table.commit();
        table.setTransaction(defaultTrasaction);
        manager.endTransaction(transactionId);
        return result;
    }

    public String get(String key) {
        table.setTransaction(localChanges);
        try {
            Storeable value = table.get(key);
            if (value == null) {
                throw new IllegalArgumentException("key not found");
            }
            return tableProvider.serialize(table, value);
        } finally {
            table.setTransaction(defaultTrasaction);
        }
    }

    public String put(String key, String value) throws IOException {
        table.setTransaction(localChanges);
        try {
            Storeable oldValue = table.put(key, tableProvider.deserialize(table, value));
            if (oldValue == null) {
                throw new IllegalArgumentException("incorrect arguments on put command");
            }
            return tableProvider.serialize(table, oldValue);
        } catch (ParseException e) {
            throw new IOException(e);
        } finally {
            table.setTransaction(defaultTrasaction);
        }
    }

    public int rollback() {
        table.setTransaction(localChanges);
        int result = table.rollback();
        table.setTransaction(defaultTrasaction);
        manager.endTransaction(transactionId);
        return result;
    }

    public int size() {
        table.setTransaction(localChanges);
        try {
            return table.size();
        } finally {
            table.setTransaction(defaultTrasaction);
        }
    }

    public String getTransactionId() {
        return transactionId;
    }
}
