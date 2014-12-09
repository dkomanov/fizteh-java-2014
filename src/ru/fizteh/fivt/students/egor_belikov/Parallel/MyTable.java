package ru.fizteh.fivt.students.egor_belikov.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static ru.fizteh.fivt.students.egor_belikov.Parallel.MySerializer.returningClass;
import static ru.fizteh.fivt.students.egor_belikov.Parallel.MySerializer.returningString;

public class MyTable implements Table {

    public TableProvider myTableProvider;
    public List<Class<?>> signature;
    private Map<String, Storeable> allMyStoreables;
    //private Map<String, Storeable> sessionChanges;
    public String currentTableName;
    public String currentPath;
    private File currentTableFile;
    public Integer numberOfElements;
    public Integer numberOfColumns;
    public Integer numberOfUnsavedChanges;
    final ThreadLocal<Map<String, Storeable>> sessionChanges =
            ThreadLocal.withInitial(()-> new HashMap<>());
    private ReadWriteLock tableOperationsLock =  new ReentrantReadWriteLock(true);
    private Map<String, Storeable> allRecords;




    public MyTable(String name, String pathname, List<Class<?>> columnTypes) {
        myTableProvider = ParallelMain.myTableProvider;
        allMyStoreables = new TreeMap<>();
        allRecords = new TreeMap<>();
        //sessionChanges = new TreeMap<>();
        currentPath = pathname + File.separator + name;
        currentTableFile = new File(currentPath);
        currentTableName = name;
        numberOfUnsavedChanges = 0;
        numberOfElements = 0;
        if (columnTypes != null) {
            signature = new ArrayList<>(columnTypes);
            numberOfColumns = signature.size();
        } else {
            signature = new ArrayList<>();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        tableOperationsLock.readLock().lock();
        try {
            Storeable s = (MyStoreable) sessionChanges.get().put(key, value);
            if (s != null) {
                return s;
            } else {
                numberOfUnsavedChanges++;
                numberOfElements++;
                return null;
            }
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        tableOperationsLock.readLock().lock();
        try {
            Storeable s = (MyStoreable) sessionChanges.get().remove(key);
            if (s != null) {
                numberOfUnsavedChanges++;
                numberOfElements--;
                return s;
            } else {
                return null;
            }
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }

    public int size() {
        return numberOfElements;
    }

    @Override
    public List<String> list() {
        tableOperationsLock.readLock().lock();
        try {
            List<String> list = new ArrayList<>();
            list.addAll(sessionChanges.get().keySet());
            return list;
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }
    public int commit() throws IOException {
        int n;
        tableOperationsLock.writeLock().lock();
        try {
            rm();
            writeSignature();
            try {
                for (Map.Entry<String, Storeable> i : sessionChanges.get().entrySet()) {
                    String key;
                    MyStoreable value;
                    key = i.getKey();
                    value = (MyStoreable) i.getValue();
                    Integer ndirectory = Math.abs(key.getBytes("UTF-8")[0] % 16);
                    Integer nfile = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
                    String pathToDir = currentPath + File.separator + ndirectory.toString()
                            + ".dir";
                    //System.out.println(pathToDir);
                    File file = new File(pathToDir);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String pathToFile = currentPath + File.separator + ndirectory.toString()
                            + ".dir" + File.separator + nfile.toString() + ".dat";
                    //System.out.println(pathToFile);
                    file = new File(pathToFile);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    DataOutputStream outStream = new DataOutputStream(
                            new FileOutputStream(pathToFile, true));
                    writeValue(outStream, key, value);
                    outStream.close();

                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
            allRecords = new TreeMap<>(sessionChanges.get());
            n = numberOfUnsavedChanges;
            numberOfUnsavedChanges = 0;
        } finally {
            tableOperationsLock.writeLock().unlock();
        }
        return n;
    }

    public void writeValue(DataOutputStream os, String key, MyStoreable value) throws IOException {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] valueBytes = myTableProvider.serialize(this, value).getBytes("UTF-8");
        os.writeInt(keyBytes.length);
        os.write(keyBytes);
        os.writeInt(valueBytes.length);
        os.write(valueBytes);
    }

    public void rm() {

        File[] dirs = this.currentTableFile.listFiles();
        if (dirs != null) {
            for (File dir : dirs) {
                if (!dir.isDirectory()) {
                    try {
                        if (dir.getName().equals("signature.tsv")) {
                            dir.delete();
                        } else {
                            throw new Exception(dir.getName()
                                    + " is not directory");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(-1);
                    }
                }
                if (dir.exists()) {
                    File[] dats = dir.listFiles();
                    assert dats != null;
                    if (dats.length == 0) {
                        System.err.println("Empty folders found");
                        System.exit(-1);
                    }
                    for (File dat : dats) {
                        if (!dat.delete()) {
                            System.out.println("Error while reading table " + currentTableName);
                        }


                    }

                    if (!dir.delete()) {
                        System.out.println("Error while reading table " + currentTableName);
                    }
                }
            }
        }
    }

    public void load() throws Exception {
        if (!currentTableFile.exists()) {
            throw new NullPointerException("Directory name is null");
        }
        readSignature();
        tableOperationsLock.writeLock().lock();
        try {
            try {
                File[] dirs = currentTableFile.listFiles();
                for (File dir : dirs) {
                    if (!dir.isDirectory() && !dir.getName().equals("signature.tsv")) {
                        System.err.println(dir.getName()
                                + " is not directory");
                        System.exit(-1);
                    }
                    if (!dir.getName().equals("signature.tsv")) {
                        File[] dats = dir.listFiles();
                        if (dats.length == 0) {
                            System.err.println("Empty folders found");
                            System.exit(-1);
                        }
                        for (File dat : dats) {
                            int nDirectory = Integer.parseInt(dir.getName().substring(0,
                                    dir.getName().length() - 4));
                            int nFile = Integer.parseInt(dat.getName().substring(0,
                                    dat.getName().length() - 4));
                            String key;
                            Path file = Paths.get(dat.getAbsolutePath());
                            try (DataInputStream fileStream = new DataInputStream(Files.newInputStream(file))) {
                                while (fileStream.available() > 0) {
                                    key = readKeyValue(fileStream);
                                    if (!(nDirectory == Math.abs(key.getBytes("UTF-8")[0] % 16))
                                            || !(nFile == Math.abs((key.getBytes("UTF-8")[0]
                                            / 16) % 16))) {
                                        System.err.println("Error while reading table " + currentTableName);
                                        System.exit(-1);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        } finally {
            tableOperationsLock.writeLock().unlock();
        }
        sessionChanges.set(allRecords);
    }

    private String readKeyValue(DataInputStream is) throws Exception {
        int keyLen = is.readInt();
        byte[] keyBytes = new byte[keyLen];
        int keyRead = is.read(keyBytes, 0, keyLen);
        if (keyRead != keyLen) {
            throw new IOException("database: db file is invalid");
        }
        int valLen = is.readInt();
        byte[] valueBytes = new byte[valLen];
        int valRead = is.read(valueBytes, 0, valLen);
        if (valRead != valLen) {
            throw new IOException("database: db file is invalid");
        }

        try {
            String key = new String(keyBytes, "UTF-8");
            Storeable value;
            value = myTableProvider.deserialize(this, new String(valueBytes, "UTF-8"));
            allMyStoreables.put(key, value);
            return key;
        } catch (ColumnFormatException e) {
            throw new ColumnFormatException("database: JSON structure is invalid");
        }
    }

    public void writeSignature() throws IOException {
        PrintWriter out = new PrintWriter(currentTableFile.toPath().resolve("signature.tsv").toString());
        for (Class<?> type : signature) {
            out.print(returningString(type));
            out.print("\t");
        }
        numberOfColumns = signature.size();
        out.close();
    }

    private void readSignature() throws IOException {
        signature.clear();
        try (BufferedReader reader = Files.newBufferedReader(currentTableFile.toPath().resolve("signature.tsv"))) {
            String line = reader.readLine();
            for (String token : line.split("\t")) {
                signature.add(returningClass(token));
            }
        } catch (Exception e) {
            throw new IOException(currentTableName + ": No signature file or it's empty");
        }
        numberOfColumns = signature.size();
    }

    public int rollback() {
        int n;
        tableOperationsLock.readLock().lock();
        try {
            sessionChanges.set(allRecords);
            n = numberOfUnsavedChanges;
            numberOfUnsavedChanges = 0;
            numberOfElements = allRecords.size();
        } finally {
            tableOperationsLock.readLock().unlock();
        }
        return n;
    }


    @Override
    public int getColumnsCount() {
        return numberOfColumns;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        return currentTableName;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        tableOperationsLock.readLock().lock();
        try {
            Storeable s = (MyStoreable) sessionChanges.get().get(key);
            return s;
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }
    @Override
    public int getNumberOfUncommittedChanges() {
        return numberOfUnsavedChanges;
    }
}