package ru.fizteh.fivt.students.ilivanov.Parallel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionPool {
    private HashMap<String, Transaction> transactions;
    private Queue<Integer> available;
    private int idLength;
    private ReadWriteLock lock;

    public TransactionPool(int idLength) {
        this.idLength = idLength;
        transactions = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        available = new LinkedList<>();
        int maxValue = 1;
        for (int i = 0; i < idLength; i++) {
            maxValue *= 10;
        }
        for (int i = 0; i < maxValue; i++) {
            available.add(i);
        }
    }

    private void checkTransactionId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Null id");
        }
        if (id.length() != idLength) {
            throw new IllegalArgumentException("Wrong id format");
        }
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') {
                throw new IllegalArgumentException(
                        String.format("Illegal character %c, must be a digit", id.charAt(i)));
            }
        }
    }

    String createTransaction(MultiFileMap table) {
        lock.writeLock().lock();
        try {
            if (available.isEmpty()) {
                throw new IllegalStateException("No space for a new transaction");
            }
            int next = available.remove();
            String id = String.format("%05d", next);
            transactions.put(id, new Transaction(table));
            return id;
        } finally {
            lock.writeLock().unlock();
        }
    }

    void removeTransaction(String id) {
        checkTransactionId(id);
        lock.writeLock().lock();
        try {
            if (transactions.remove(id) == null) {
                throw new IllegalArgumentException("Transaction doesn't exist");
            }
            available.add(Integer.parseInt(id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    Transaction getTransaction(String id) {
        checkTransactionId(id);
        lock.readLock().lock();
        try {
            Transaction result = transactions.get(id);
            if (result == null) {
                throw new IllegalArgumentException("Transaction doesn't exist");
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    TransactionHandler createHandler(MultiFileMap table) {
        return new TransactionHandler(table, this);
    }
}