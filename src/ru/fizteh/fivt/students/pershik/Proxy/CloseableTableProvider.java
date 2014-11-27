package ru.fizteh.fivt.students.pershik.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pershik.Storeable.NameChecker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pershik on 11/27/14.
 */
public class CloseableTableProvider implements TableProvider, AutoCloseable {
    private final String signatureFileName = "signature.tsv";

    private Map<Class<?>, String> classNames;
    private Map<String, Class<?>> revClassNames;
    private String dbDirPath;
    private Map<String, CloseableTable> tables;
    private ReentrantReadWriteLock lock;
    private boolean closed;

    public CloseableTableProvider(String dbDir)
            throws IOException {
        dbDirPath = dbDir;
        tables = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        initClassNames();
        initProvider();
    }

    @Override
    public CloseableTable getTable(String name)
            throws IllegalArgumentException {
        checkClosed();
        if (!NameChecker.checkName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        lock.readLock().lock();
        try {
            if (tables.containsKey(name) && !tables.get(name).isClosed()) {
                return tables.get(name);
            } else {
                return null;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public CloseableTable createTable(String name, List<Class<?>> columnTypes)
            throws IOException, IllegalArgumentException {
        checkClosed();
        if (!NameChecker.checkName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("Signature can't be null");
        }
        try {
            new MyStoreableEntry(columnTypes);
        } catch (ColumnFormatException e) {
            throw new IllegalArgumentException(e);
        }
        lock.writeLock().lock();
        try {
            if (tables.containsKey(name) && tables.get(name).isClosed()) {
                return new CloseableTable(tables.get(name));
            } else if (tables.containsKey(name)) {
                return null;
            } else {
                String tableDirPath = dbDirPath + File.separator + name;
                File tableDir = new File(tableDirPath);
                if (!tableDir.mkdir()) {
                    throw new IOException("Can't create this table");
                }
                File signatureFile = new File(tableDirPath
                        + File.separator + signatureFileName);
                writeSignature(signatureFile, columnTypes);
                CloseableTable table = new CloseableTable(this, name, dbDirPath, columnTypes, lock);
                tables.put(name, table);
                return table;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name)
            throws IOException, IllegalArgumentException, IllegalStateException {
        checkClosed();
        if (!NameChecker.checkName(name)) {
            throw new IllegalArgumentException("Invalid table name");
        }
        lock.writeLock().lock();
        try {
            if (tables.containsKey(name)) {
                CloseableTable table = tables.get(name);
                table.removeFromDisk(true);
                String tablePath = dbDirPath + File.separator + name;
                File tableDir = new File(tablePath);
                String signaturePath = tablePath + File.separator + signatureFileName;
                File signatureFile = new File(signaturePath);
                if (!signatureFile.delete() || !tableDir.delete()) {
                    throw new IOException("Can't remove table");
                }
                tables.remove(name);
            } else {
                throw new IllegalStateException("Table doesn't exist");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public MyStoreableEntry deserialize(Table table, String value)
            throws ParseException {
        checkClosed();
        if (!value.startsWith("<row>")) {
            throw new ParseException("<row> expected", 0);
        } else if (!value.endsWith("</row>")) {
            throw new ParseException("</row> expected", value.length() - 1);
        }
        value = value.substring(5, value.length() - 6);
        MyStoreableEntry storeable = createFor(table);
        for (int columnNumber = 0; columnNumber < table.getColumnsCount();
             columnNumber++) {
            if (value.startsWith("<null/>")) {
                value = value.substring(7);
            } else if (value.startsWith("<col>")) {
                value = value.substring(5);
                int pos = value.indexOf("</col>");
                if (pos == -1) {
                    errorIncorrectXmlSyntax();
                } else {
                    String column = value.substring(0, pos);
                    storeable.setColumnAt(columnNumber,
                            parseObject(column, table.getColumnType(columnNumber)));
                    value = value.substring(pos + 6);
                }
            } else {
                errorIncorrectXmlSyntax();
            }
        }
        if (!"".equals(value)) {
            errorIncorrectFormat();
        }
        return storeable;
    }

    private Object parseObject(String str, Class<?> type) throws ParseException {
        try {
            if (type == Integer.class) {
                return Integer.parseInt(str);
            } else if (type == Long.class) {
                return Long.parseLong(str);
            } else if (type == Byte.class) {
                return Byte.parseByte(str);
            } else if (type == Float.class) {
                return Float.parseFloat(str);
            } else if (type == Double.class) {
                return Double.parseDouble(str);
            } else if (type == Boolean.class) {
                if (!"true".equals(str) && !"false".equals(str)) {
                    errorIncorrectFormat();
                }
                return Boolean.parseBoolean(str);
            } else {
                return str;
            }
        } catch (NumberFormatException e) {
            errorIncorrectFormat();
        }
        return str;
    }

    private void errorIncorrectXmlSyntax() throws ParseException {
        throw new ParseException("Incorrect xml syntax", 0);
    }

    private void errorIncorrectFormat() throws ParseException {
        throw new ParseException("Incorrect storeable format", 0);
    }

    @Override
    public String serialize(Table table, Storeable value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        checkFormat(table, value);
        StringBuilder serialized = new StringBuilder("<row>");
        for (int columnNumber = 0; columnNumber < table.getColumnsCount();
             columnNumber++) {
            Object column = value.getColumnAt(columnNumber);
            if (column == null) {
                serialized.append("<null/>");
            } else {
                serialized.append("<col>");
                if (column.getClass() == String.class) {
                    serialized.append(column);
                } else {
                    serialized.append(serialize(column));
                }
                serialized.append("</col>");
            }
        }
        serialized.append("</row>");
        return serialized.toString();
    }

    private String serialize(Object value) {
        if (value.getClass() == Integer.class) {
            return Integer.toString((Integer) value);
        } else if (value.getClass() == Long.class) {
            return Long.toString((Long) value);
        } else if (value.getClass() == Byte.class) {
            return Byte.toString((Byte) value);
        } else if (value.getClass() == Float.class) {
            return Float.toString((Float) value);
        } else if (value.getClass() == Double.class) {
            return Double.toString((Double) value);
        } else if (value.getClass() == Boolean.class) {
            return Boolean.toString((Boolean) value);
        } else {
            return (String) value;
        }
    }

    @Override
    public MyStoreableEntry createFor(Table table) {
        checkClosed();
        List<Class<?>> signature = new ArrayList<>();
        for (int columnNumber = 0; columnNumber < table.getColumnsCount();
             columnNumber++) {
            signature.add(table.getColumnType(columnNumber));
        }
        return new MyStoreableEntry(signature);
    }

    @Override
    public MyStoreableEntry createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        if (values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Invalid number of values");
        }
        MyStoreableEntry res = createFor(table);
        for (int columnNumber = 0; columnNumber < values.size(); columnNumber++) {
            if (!table.getColumnType(columnNumber).equals(
                    values.get(columnNumber).getClass())) {
                throw new ColumnFormatException("Invalid values format");
            }
            res.setColumnAt(columnNumber, values.get(columnNumber));
        }
        return res;
    }

    @Override
    public List<String> getTableNames() {
        checkClosed();
        lock.readLock().lock();
        try {
            return new ArrayList<String>(tables.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<Class<?>> readSignature(File signatureFile)
            throws IOException {
        lock.readLock().lock();
        try {
            try (Scanner scanner = new Scanner(signatureFile)) {
                List<Class<?>> signature = new ArrayList<>();
                while (scanner.hasNext()) {
                    String str = scanner.next();
                    signature.add(revClassNames.get(str));
                }
                return signature;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void writeSignature(File signatureFile, List<Class<?>> signature)
            throws IOException {
        lock.writeLock().lock();
        try {
            try (PrintWriter writer = new PrintWriter(signatureFile)) {
                for (Class<?> cl : signature) {
                    writer.print(classNames.get(cl) + " ");
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void checkFormat(Table table, Storeable storeable)
            throws IndexOutOfBoundsException, ColumnFormatException {
        for (int columnNumber = 0; columnNumber < table.getColumnsCount();
             columnNumber++) {
            Object value = storeable.getColumnAt(columnNumber);
            if (value != null
                    && !value.getClass().equals(table.getColumnType(columnNumber))) {
                throw new ColumnFormatException("Invalid storeable format");
            }
        }
    }

    private void initClassNames() {
        classNames = new HashMap<>();
        classNames.put(Integer.class, "int");
        classNames.put(Long.class, "long");
        classNames.put(Byte.class, "byte");
        classNames.put(Float.class, "float");
        classNames.put(Double.class, "double");
        classNames.put(Float.class, "float");
        classNames.put(Boolean.class, "boolean");
        classNames.put(String.class, "String");

        revClassNames = new HashMap<>();
        for (Class<?> cl : classNames.keySet()) {
            String name = classNames.get(cl);
            revClassNames.put(name, cl);
        }
    }

    private void initProvider() throws IOException {
        File dbDir = new File(dbDirPath);
        for (File curDir : dbDir.listFiles()) {
            if (curDir.isDirectory()) {
                File signatureFile = new File(
                        curDir.getAbsolutePath() + File.separator + signatureFileName);
                List<Class<?>> signature = readSignature(signatureFile);
                CloseableTable table = new CloseableTable(
                        this, curDir.getName(), dbDirPath, signature, lock);
                tables.put(curDir.getName(), table);
            } else {
                throw new IOException("Directory contains incorrect files");
            }
        }
    }

    public boolean contains(String name) {
        checkClosed();
        lock.readLock().lock();
        try {
            return tables.containsKey(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, CloseableTable> getAllTables() {
        checkClosed();
        lock.readLock().lock();
        try {
            return tables;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws Exception {
        checkClosed();
        lock.readLock().lock();
        try {
            for (CloseableTable table : tables.values()) {
                table.close();
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        closed = true;
        lock.writeLock().unlock();
    }

    private void checkClosed() {
        lock.readLock().lock();
        try {
            if (closed) {
                throw new IllegalStateException("Provider is closed");
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        File dbDir = new File(dbDirPath);
        return this.getClass().getSimpleName() + "["
                + dbDir.getAbsolutePath() + "]";
    }
}
