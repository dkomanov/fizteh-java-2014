package ru.fizteh.fivt.students.ryad0m.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StructedTable {

    private DirTable dirTable;
    private Path location;
    private List<Class<?>> columnTypes;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private AtomicBoolean closed = new AtomicBoolean(false);

    public StructedTable(Path path) throws IOException {
        location = path;
        dirTable = new DirTable(path);
        File columnFile = location.resolve("signature.tsv").toFile();
        try (FileInputStream fis = new FileInputStream(columnFile)) {
            Scanner scanner = new Scanner(fis);
            columnTypes = Typer.typeListFromString(scanner.nextLine());
            scanner.close();
            fis.close();
        } catch (Exception ex) {
            throw new IOException("can't load signature.tsv");
        }
    }

    public StructedTable(Path path, List<Class<?>> columnTypes) throws IOException {
        location = path;
        deleteData();
        dirTable = new DirTable(path);
        this.columnTypes = columnTypes;
        save();
    }


    public String getName() {
        return location.getFileName().toString();
    }

    private void deleteDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    public void deleteData() {
        lock.writeLock().lock();
        try {
            deleteDir(location.toFile());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void save() throws IOException {
        lock.writeLock().lock();
        try {
            deleteData();
            location.toFile().mkdirs();
            dirTable.save();
            File columnFile = location.resolve("signature.tsv").toFile();
            try (FileOutputStream fos = new FileOutputStream(columnFile)) {
                PrintStream printStream = new PrintStream(fos);
                printStream.print(Typer.stringFromTypeList(columnTypes));
                printStream.close();
                fos.close();
            } catch (Exception ex) {
                throw new IOException("can't save signature.tsv");
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public void put(String key, Storeable value) {
        lock.writeLock().lock();
        try {
            if (value == null) {
                dirTable.put(key, null);
            } else {
                checkIntegrity(value);
                dirTable.put(key, XmlSerializer.serializeObjectList(((MyStorable) value).getValues()));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void checkIntegrity(Storeable value) {
        lock.readLock().lock();
        try {
            List<Class<?>> passed = ((MyStorable) value).getTypes();
            if (passed.size() != columnTypes.size()) {
                throw new ColumnFormatException("Incorrect number of values to serialize for table.");
            } else {
                for (int i = 0; i < passed.size(); ++i) {
                    if (passed.get(i) != columnTypes.get(i)) {
                        throw new ColumnFormatException("Incorrect types.");
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public boolean containKey(String key) {
        boolean res;
        lock.readLock().lock();
        res = dirTable.containKey(key);
        lock.readLock().unlock();
        return res;
    }

    public Storeable get(String key) throws ParseException {
        if (containKey(key)) {
            Storeable res;
            lock.readLock().lock();
            try {
                res = new MyStorable(columnTypes, XmlSerializer.deserializeString(dirTable.get(key), columnTypes));
            } finally {
                lock.readLock().unlock();
            }
            return res;
        } else {
            return null;
        }
    }

    public void remove(String key) {
        lock.writeLock().lock();
        try {
            dirTable.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getSize() {
        int res;
        lock.readLock().lock();
        res = dirTable.getSize();
        lock.readLock().unlock();
        return res;
    }

    public Set<String> getKeys() {
        Set<String> res;
        lock.readLock().lock();
        res = dirTable.getKeys();
        lock.readLock().unlock();
        return res;
    }
}
