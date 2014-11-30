package ru.fizteh.fivt.students.standy66_new.server.tdb;

import ru.fizteh.fivt.students.standy66_new.storage.FileMap;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableSignature;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class TransactionDbImpl implements TransactionDb {
    private static final int MAX_FILE_CHUNK = 16;
    private static final int MAX_DIR_CHUNK = 16;

    private File dbFile;
    private Map<String, Map<String, TableRow>> cachedData;
    private Map<String, TableSignature> signatures;
    private ReadWriteLock dataLock = new ReentrantReadWriteLock();

    public TransactionDbImpl(File dbFile) throws IOException, ParseException {
        if (dbFile == null) {
            throw new IllegalArgumentException("path is null");
        }
        this.dbFile = dbFile;
        signatures = new HashMap<>();
        cachedData = new HashMap<>();
        for (File tableDir: dbFile.listFiles()) {
            File signatureFile = new File(tableDir, "signature.tsv");
            TableSignature tableSignature = TableSignature.readFromFile(signatureFile);
            Map<String, TableRow> tableData = new HashMap<>();
            signatures.put(tableDir.getName(), tableSignature);
            cachedData.put(tableDir.getName(), tableData);
            for (int i = 0; i < MAX_DIR_CHUNK; i++) {
                File dirChunk = new File(tableDir, i + ".dir");
                if (!dirChunk.exists()) {
                    continue;
                }
                if (!dirChunk.isDirectory()) {
                    throw new IllegalStateException("dirChunk is file");
                }
                for (int j = 0; j < MAX_FILE_CHUNK; j++) {
                    File fileChunk = new File(dirChunk, j + ".dat");
                    if (!fileChunk.exists()) {
                        continue;
                    }
                    if (!fileChunk.isFile()){
                        throw new IllegalStateException("fileChunk is dir");
                    }
                    FileMap fileMap = new FileMap(fileChunk);
                    for (String key: fileMap.keySet()) {
                        String value = fileMap.get(key);
                        tableData.put(key, TableRow.deserialize(tableSignature, value));
                    }
                }
            }
        }
    }

    @Override
    public Transaction beginTransaction(final String tableName) {
        return new Transaction() {
            private Set<String> deletedKeys = new HashSet<>();
            private Map<String, TableRow> changedKeys = new HashMap<>();

            private int getDiffSize() {
                return changedKeys.size() + deletedKeys.size();
            }

            @Override
            public TableSignature getSignature() {
                return signatures.get(tableName);
            }

            @Override
            public int commit() throws IOException {
                int result = getDiffSize();
                dataLock.writeLock().lock();
                try {
                    apply(cachedData.get(tableName));
                    writeChanges(tableName);
                } finally {
                    dataLock.writeLock().unlock();
                }
                return result;
            }

            @Override
            public int rollback() {
                int result = getDiffSize();
                deletedKeys.clear();
                changedKeys.clear();
                return result;
            }

            @Override
            public TableRow get(String key) {
                if (deletedKeys.contains(key)) {
                    return null;
                }
                if (changedKeys.containsKey(key)) {
                    return changedKeys.get(key);
                }
                dataLock.readLock().lock();
                try {
                    return cachedData.get(tableName).get(key);
                } finally {
                    dataLock.readLock().unlock();
                }
            }

            @Override
            public TableRow put(String key, TableRow value) {
                TableRow oldValue = get(key);
                if (deletedKeys.contains(key)) {
                    deletedKeys.remove(key);
                }
                changedKeys.put(key, value);
                return oldValue;
            }

            @Override
            public TableRow remove(String key) {
                TableRow oldValue = get(key);
                if (changedKeys.containsKey(key)) {
                    changedKeys.remove(key);
                }
                deletedKeys.add(key);
                return oldValue;
            }

            @Override
            public int size() {
                Map<String, TableRow> copy = new HashMap<>(cachedData.get(tableName));
                apply(copy);
                return copy.size();
            }

            private Map<String, TableRow> apply(Map<String, TableRow> base) {
                for (String key : deletedKeys) {
                    base.remove(key);
                }
                for (Map.Entry<String, TableRow> entry : changedKeys.entrySet()) {
                    base.put(entry.getKey(), entry.getValue());
                }
                return base;
            }
        };
    }

    private void writeChanges(String table) throws IOException {
        File tableDir = new File(dbFile, table);
        Map<File, FileMap> fileMapMap = new HashMap<>();
        for (int i = 0; i < MAX_DIR_CHUNK; i++) {
            File dirChunk = new File(tableDir, i + ".dir");
            if (!dirChunk.exists()) {
                continue;
            }
            if (!dirChunk.isDirectory()) {
                throw new IllegalStateException("dirChunk is file");
            }
            for (int j = 0; j < MAX_FILE_CHUNK; j++) {
                File fileChunk = new File(dirChunk, j + ".dat");
                if (!fileChunk.exists()) {
                    continue;
                }
                if (!fileChunk.isFile()){
                    throw new IllegalStateException("fileChunk is dir");
                }
                FileMap fileMap = new FileMap(fileChunk);
                fileMap.clear();
                for (Map.Entry<String, TableRow> entry: cachedData.get(table).entrySet()) {
                    fileMap.put(entry.getKey(), entry.getValue().serialize());
                }
                fileMap.commit();
            }
        }
    }

    @Override
    public boolean createTable(String tableName) {
        return false;
    }

    @Override
    public boolean removeTable(String tableName) {
        return false;
    }
}
