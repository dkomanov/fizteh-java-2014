package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;
import ru.fizteh.fivt.students.dsalnikov.utils.CountingTools;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class StorableTable implements Table, AutoCloseable, Serializable {

    private final Lock lockForTransactions = new ReentrantLock(true);
    //shared across threads
    private File tableFileDir;
    private TableProvider databaseTableProvider;
    private List<Class<?>> columnTypes;
    private Map<String, Storeable> data;
    //single thread data
    private volatile WorkStatus containerState;
    private ThreadLocal<TransactionChanges> transactionChanges;
    //   private ThreadLocal<Map<String, Storeable>> changes;
    //   private ThreadLocal<Set<String>> removed;
    //   private ThreadLocal<Integer> changesCounter;

    public StorableTable(File tableDir, TableProvider tableProvider) throws IOException {
        data = new HashMap<>();
        containerState = WorkStatus.NOT_INITIALIZED;
        //incorrectass java 8 code
//        changes = new ThreadLocal<>().withInitial(HashMap::new);
//        removed = new ThreadLocal<>().withInitial(HashSet::new);
//        changesCounter = new ThreadLocal<>().withInitial(() -> 0);
        transactionChanges = new ThreadLocal<>().withInitial(TransactionChanges::new);
        databaseTableProvider = tableProvider;
        tableFileDir = tableDir;
        if (tableProvider == null) {
            throw new IOException("provided tableProvider is incorrect");
        }
        try {
            containerState = WorkStatus.WORKING;
            FileMapUtils.readIntoDataBase(tableFileDir, data, this, tableProvider);
        } catch (IOException | ParseException exc) {
            throw new IllegalArgumentException("Reading from file failed", exc);
        }
    }

    public StorableTable(File dataDirectory, List<Class<?>> givenTypes, TableProvider givenProvider)
            throws IOException {
        if (givenProvider == null) {
            throw new IOException("storeable table: create failed, provider is not set");
        }

        data = new HashMap<>();
        containerState = WorkStatus.NOT_INITIALIZED;
//        changes = new ThreadLocal<>().withInitial(HashMap::new);
//        removed = new ThreadLocal<>().withInitial(HashSet::new);
//        changesCounter = new ThreadLocal<>().withInitial(() -> 0);
        transactionChanges = new ThreadLocal<>().withInitial(TransactionChanges::new);
        databaseTableProvider = givenProvider;
        tableFileDir = dataDirectory;
        columnTypes = givenTypes;
        containerState = WorkStatus.WORKING;
        FileMapUtils.createSignatureFile(tableFileDir, this);
    }

    @Override
    public String getName() {
        containerState.canBeSafelyUsed();
        return tableFileDir.getName();
    }

    @Override
    public Storeable get(String key) {
        WorkStatus status = containerState;
        status.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectArgument(key)) {
            throw new IllegalArgumentException("get: key is incorrect");
        }
        Storeable resultOfGet = transactionChanges.get().getFromTransaction(key);
        if (resultOfGet == null) {
            if (transactionChanges.get().isKeyWasRemovedFromTransaction(key)) {
                return null;
            }
            lockForTransactions.lock();
            try {
                resultOfGet = data.get(key);
            } finally {
                lockForTransactions.unlock();
            }
        }
        return resultOfGet;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        WorkStatus status = containerState;
        status.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectArgument(key)) {
            throw new IllegalArgumentException("put: key is incorrect");
        }
/*        if (!CorrectnessCheck.correctStoreable(value, this.columnTypes)) {
/*            throw new ColumnFormatException("put: value not suitable for this table");
        }*/
        Storeable valueInData;
        lockForTransactions.lock();
        try {
            valueInData = data.get(key);
        } finally {
            lockForTransactions.unlock();
        }
        Storeable resultOfPut = transactionChanges.get().putIntoTransaction(key, value);

        if (resultOfPut == null) {
            transactionChanges.get().incrementChangesCounter();
            if (!transactionChanges.get().isKeyWasRemovedFromTransaction(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            transactionChanges.get().addKeyToRemovedKeySet(key);
        }
        return resultOfPut;
    }

    @Override
    public Storeable remove(String key) {
        containerState.canBeSafelyUsed();
        if (!CorrectnessCheck.isCorrectArgument(key)) {
            throw new IllegalArgumentException("remove: key is incorrect");
        }
        Storeable resultOfRemove = transactionChanges.get().getFromTransaction(key);
        if (resultOfRemove == null && !transactionChanges.get().isKeyWasRemovedFromTransaction(key)) {
            lockForTransactions.lock();
            try {
                resultOfRemove = data.get(key);
            } finally {
                lockForTransactions.unlock();
            }
        }
        if (transactionChanges.get().isKeyInTransaction(key)) {
            transactionChanges.get().decrementChangesCounter();
            transactionChanges.get().removeFromTransaction(key);
            lockForTransactions.lock();
            try {
                if (data.containsKey(key)) {
                    transactionChanges.get().addKeyToRemovedKeySet(key);
                }
            } finally {
                lockForTransactions.unlock();
            }
        } else {
            lockForTransactions.lock();
            try {
                if (data.containsKey(key) && !transactionChanges.get().isKeyWasRemovedFromTransaction(key)) {
                    transactionChanges.get().addKeyToRemovedKeySet(key);
                    transactionChanges.get().incrementChangesCounter();
                }
            } finally {
                lockForTransactions.unlock();
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        containerState.canBeSafelyUsed();
        lockForTransactions.lock();
        try {
            return CountingTools.storableCountSize(this.data, transactionChanges.get().getRawTransacttionChanges(), transactionChanges.get().getRawRemoved());
        } finally {
            lockForTransactions.unlock();
        }
    }

    @Override
    public List<String> list() {
        Set<String> keySet = new HashSet<>(transactionChanges.get().getRawTransacttionChanges().keySet());
        keySet.addAll(data.keySet());
        return keySet.stream().collect(Collectors.toList());
    }

    @Override
    public int commit() {
        containerState.canBeSafelyUsed();
        int result = -1;
        lockForTransactions.lock();
        try {
            result = CountingTools.countingOfChangesInStoreable(this, data, transactionChanges.get().getRawTransacttionChanges(), transactionChanges.get().getRawRemoved());
            transactionChanges.get().getRawRemoved().forEach(data::remove);
            data.putAll(transactionChanges.get().getRawTransacttionChanges());
            try {
                FileMapUtils.writeIntoFiles(tableFileDir, data, this, databaseTableProvider);
            } catch (Exception exc) {
                System.err.println("commit: " + exc.getMessage());
            }
        } finally {
            lockForTransactions.unlock();
        }
        setDefault();
        return result;
    }

    @Override
    public int rollback() {
        containerState.canBeSafelyUsed();
        int result = -1;
        lockForTransactions.lock();
        try {
            result = CountingTools.countingOfChangesInStoreable(this, data, transactionChanges.get().getRawTransacttionChanges(), transactionChanges.get().getRawRemoved());
        } finally {
            lockForTransactions.unlock();
        }
        setDefault();
        return result;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return CountingTools.countingOfChangesInStoreable(this, data, transactionChanges.get().getRawTransacttionChanges(), transactionChanges.get().getRawRemoved());
    }

    @Override
    public int getColumnsCount() {
        containerState.canBeSafelyUsed();
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        containerState.canBeSafelyUsed();
        int columnsCount = getColumnsCount();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("get column type: incorrect index");
        }
        return columnTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        containerState.canBeSafelyUsed();
        return columnTypes;
    }

    @Override
    public String toString() {
        containerState.canBeSafelyUsed();
        if (tableFileDir != null) {
            return getClass().getSimpleName() + "[" + tableFileDir + "]";
        } else {
            return "NOT INITIALIZED FOLDER. PLEASE FIX THIS BEFORE USING";
        }
    }

    @Override
    public void close() {
        containerState.canBeClosed();
        if (containerState == WorkStatus.WORKING) {
            rollback();
        }
        containerState = WorkStatus.CLOSED;
    }

    private void setDefault() {
        transactionChanges.get().setDefault();
    }

    public boolean isOkForOperations() {
        try {
            if (containerState == null) {
                return false;
            }
            containerState.canBeSafelyUsed();
        } catch (IllegalStateException exc) {
            return false;
        }
        return true;
    }

    public int getAmountOfChanges() {
        containerState.canBeSafelyUsed();
        return transactionChanges.get().getChangesCounter();
    }

    public void useColumnTypes(List<Class<?>> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public int getTableDimensions() {
        return columnTypes.size();
    }

    public TransactionChanges getTransaction() {
        return transactionChanges.get();
    }

    public void setTransaction(TransactionChanges transaction) {
        transactionChanges.set(transaction);
    }
}
