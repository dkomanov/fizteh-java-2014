package ru.fizteh.fivt.students.SukhanovZhenya.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class STable implements Table {
    private ThreadLocal<Map<String, String>> fMap = new ThreadLocal<>();
    private ThreadLocal<Map<String, String>> rollBackMap = new ThreadLocal<>();
    private File tableDir;
    private List<Class<?>> typesList;
    private File typesFile;
    private int changes;
    private ReentrantLock lock;

    STable(String path, List<Class<?>> columnTypes, ReentrantLock newLock) {
        fMap.set(new HashMap<>());
        rollBackMap.set(new HashMap<>());
        lock = newLock;
        try {
            tableDir = new File(path);
            typesFile = new File(path + "/" + "signature.tsv");

            if (!typesFile.createNewFile()) {
                throw new ExceptionInInitializerError("Can't create signature.tsv");
            }

            typesList = columnTypes;
            RandomAccessFile write = new RandomAccessFile(typesFile.getAbsolutePath(), "rw");


            for (Class<?> type : columnTypes) {
                write.write(type.getSimpleName().getBytes());
            }

        } catch (SecurityException | IOException | ExceptionInInitializerError e) {
            incorrectFile();
        }
        changes = 0;
    }

    STable(String path, ReentrantLock newLock) {
        fMap.set(new HashMap<>());
        rollBackMap.set(new HashMap<>());
        lock = newLock;

        try {
            tableDir = new File(path);
            if (!tableDir.exists()) {
                throw new ExceptionInInitializerError("Incorrect Table");
            }
            typesFile = new File(path + "/" + "signature.tsv");
            if (!typesFile.exists()) {
                throw new ExceptionInInitializerError("Does not exist signature.tsv");
            }

            getFile();

        } catch (SecurityException | ExceptionInInitializerError e) {
            incorrectFile();
        }
        changes = 0;
    }

    @Override
    public String getName() {
        return tableDir.getName();
    }


    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null argument!");
        } else {
            SStoreable value = null;
            try {
                lock.lock();
                String result = fMap.get().get(key);
                if (result == null) {
                    return null;
                }
                value = new SStoreable(XMLCoder.deserializeString(result, typesList), typesList);
            } finally {
                lock.unlock();
            }

            return value;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        SStoreable result = null;
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null argument!");
        } else {
            try {
                lock.lock();
                ++changes;
                String old = fMap.get().put(key,
                        XMLCoder.serializeObjects(((SStoreable) value).getObjects()));
                if (old == null) {
                    if (!lock.isLocked()) {
                        lock.unlock();
                    }
                    return null;
                } else {
                    result = new SStoreable(XMLCoder.deserializeString(old,
                            ((SStoreable) value).getTypes()), typesList);
                }
            } finally {
                if (!lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
        return result;
    }


    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null argument!");
        }
        SStoreable value = null;
        try {
            lock.lock();
            ++changes;
            value = new SStoreable(XMLCoder.deserializeString(fMap.get().remove(key),
                    typesList), typesList);
        } finally {
            lock.unlock();
        }
        return value;
    }

    @Override
    public List<String> list() {
        List<String> result = null;
        try {
            lock.lock();
            result = new ArrayList<>(fMap.get().keySet());
        } finally {
            lock.unlock();
        }
        return result;
    }

    void getFile() {
        try {
            if (tableDir == null || tableDir.list() == null) {
                return;
            }
            lock.lock();
            fMap.get().clear();
            typesFile = new File(tableDir.getAbsolutePath() + "/" + "signature.tsv");
            if (!typesFile.exists() || !typesFile.isFile()) {
                lock.unlock();
                throw new ExceptionInInitializerError("Wrong typeFile");
            }

            try {
                Scanner reader = new Scanner(new FileReader(typesFile));
                String[] getTypes = reader.nextLine().split(" +");
                typesList = new Vector<>(getTypes.length);
                for (String name : getTypes) {
                    switch (name) {
                        case "Integer":
                            typesList.add(Integer.class);
                            break;
                        case "Long":
                            typesList.add(Long.class);
                            break;
                        case "Byte":
                            typesList.add(Byte.class);
                            break;
                        case "Float":
                            typesList.add(Float.class);
                            break;
                        case "Double":
                            typesList.add(Double.class);
                            break;
                        case "Boolean":
                            typesList.add(Boolean.class);
                            break;
                        case "String":
                            typesList.add(String.class);
                            break;
                        default:
                            lock.unlock();
                            incorrectFile();
                    }
                }

            } catch (FileNotFoundException e) {
                lock.unlock();
                incorrectFile();
            }


            for (String dirName : tableDir.list()) {
                File subDir = new File(tableDir.getAbsolutePath() + "/" + dirName);
                if (subDir.list() != null && subDir.isDirectory()) {
                    for (String fileName : subDir.list()) {
                        readFile(subDir.getAbsolutePath() + "/" + fileName, Integer.parseInt(dirName.split(".dir")[0]),
                                Integer.parseInt(fileName.split(".dat")[0]));
                    }
                }
            }
        } finally {
            changes = 0;
            rollBackMap.set(fMap.get());
            lock.unlock();
        }
    }

    private void readFile(String path, int firstHash, int secondHash) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            incorrectFile();
        }
        try {
            int sizeLength;
            long readied = 1;
            try {
                while (true) {
                    assert randomAccessFile != null;
                    sizeLength = randomAccessFile.readInt();
                    readied += 4;

                    byte[] key = new byte[sizeLength];

                    try {
                        randomAccessFile.readFully(key);
                        readied += sizeLength;
                        sizeLength = randomAccessFile.readInt();
                        readied += 4;
                        byte[] value = new byte[sizeLength];
                        randomAccessFile.readFully(value);
                        readied += sizeLength;
                        if (Math.abs((new String(key, "UTF-8")).hashCode()) % 16 != firstHash
                                || (Math.abs((new String(key, "UTF-8")).hashCode()) / 16) % 16 != secondHash) {
                            incorrectFile("Incorrect files\n" +
                                    new String(key, "UTF-8") + " " + firstHash + " " + secondHash);
                        }
                        fMap.get().put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                    } catch (EOFException e) {
                        randomAccessFile.close();
                        incorrectFile(e.getMessage());
                    }
                }
            } catch (EOFException e) {
                if (readied < randomAccessFile.length()) {
                    incorrectFile();
                }
                randomAccessFile.close();
            }
        } catch (IOException | OutOfMemoryError ioe) {
            incorrectFile(ioe.getMessage());
        }
    }

    @Override
    public int commit() throws IOException {
        if (tableDir == null) {
            return 0;
        }
        try {
            lock.lock();
            for (int i = 0; i < 16; ++i) {
                for  (int j = 0; j < 16; ++j) {
                    File dir = new File(tableDir.getAbsolutePath() + "/" + i + ".dir");
                    if (!dir.exists()) {
                        if (!dir.mkdir()) {
                            incorrectFile("Can not create a directory");
                        }
                    }
                    RandomAccessFile tmp =
                            new RandomAccessFile(tableDir.getAbsoluteFile() + "/" + i + ".dir/" + j + ".dat", "rw");
                    tmp.setLength(0);
                    tmp.close();
                }
            }

            for (Map.Entry<String, String> pair : fMap.get().entrySet()) {
                if (pair.getValue() != null) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(tableDir.getAbsoluteFile() + "/"
                            + (Math.abs(pair.getKey().hashCode()) % 16) + ".dir/"
                            + ((Math.abs(pair.getKey().hashCode()) / 16) % 16) + ".dat", "rw");

                    randomAccessFile.setLength(0);
                    randomAccessFile.writeInt(pair.getKey().getBytes().length);
                    randomAccessFile.write(pair.getKey().getBytes("UTF-8"));
                    randomAccessFile.writeInt(pair.getValue().getBytes("UTF-8").length);
                    randomAccessFile.write(pair.getValue().getBytes("UTF-8"));
                    randomAccessFile.close();
                }
            }
        } catch (IOException | OutOfMemoryError ioe) {
            incorrectFile(ioe.getMessage());
        } finally {
            deleteEmptyFiles();
            rollBackMap.set(fMap.get());
            changes = 0;
            lock.unlock();
        }

        return size();
    }

    private void deleteEmptyFiles() throws IOException {
        if (tableDir.list() == null) {
            return;
        }

        for (String dirName : tableDir.list()) {
            File dir = new File(tableDir.getAbsolutePath() + "/" + dirName);
            if (dir.list() == null) {
                return;
            }
            boolean isEmpty = true;
            for (String fileName : dir.list()) {
                File hashFile = new File(dir.getAbsolutePath() + "/" + fileName);
                if (hashFile.length() == 0) {
                    if (!hashFile.delete()) {
                        incorrectFile("Can not remove file!\n" + hashFile.getAbsolutePath());
                    }
                } else {
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                if (!dir.delete()) {
                    throw new IOException("Can not remove directory! " + dir.getAbsolutePath());
                }
            }
        }
    }

    private void incorrectFile() {
        throw new ExceptionInInitializerError("Incorrect files");
    }

    private void incorrectFile(String s) {
        throw new ExceptionInInitializerError(s);
    }

    @Override
    public int size() {
        int result = 0;
        try {
            lock.lock();
            result = fMap.get().size();
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public int rollback() {
        lock.lock();
        int result = changes;
        changes = 0;
        fMap.set(rollBackMap.get());
        lock.unlock();
        return result;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return changes;
    }

    @Override
    public int getColumnsCount() {
        lock.lock();
        int result = typesList.size();
        lock.unlock();
        return result;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= typesList.size()) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        lock.lock();
        Class<?> result = typesList.get(columnIndex);
        lock.unlock();
        return result;
    }

    public List<Class<?>> getTypesList() {
        return typesList;
    }

    public void remove() {
        lock.lock();
        tableDelete(tableDir.getAbsolutePath());
        lock.unlock();
    }

    private void tableDelete(String path) {
        File tmp = new File(path);
        if (tmp.isDirectory() && tmp.list() != null) {
            for (String name : tmp.list()) {
                tableDelete(tmp.getAbsolutePath() + "/" + name);
            }
        }
        if (!tmp.delete()) {
            lock.unlock();
            incorrectFile("Can not delete file " + tmp.getAbsoluteFile());
        }
    }
}
