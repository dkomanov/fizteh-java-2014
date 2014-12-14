package ru.fizteh.fivt.students.egor_belikov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ru.fizteh.fivt.students.egor_belikov.Storeable.MySerializer.returningString;
import static ru.fizteh.fivt.students.egor_belikov.Storeable.MySerializer.returningClass;

public class MyTable implements Table {

    public TableProvider myTableProvider;
    public List<Class<?>> signature;
    private Map<String, Storeable> allMyStoreables;
    private Map<String, Storeable> sessionChanges;
    public String currentTableName;
    public String currentPath;
    private File currentTableFile;
    public Integer numberOfElements;
    public Integer numberOfColumns;
    public Integer numberOfUnsavedChanges;


    public MyTable(String name, String pathname, List<Class<?>> columnTypes) {
        myTableProvider = StoreableMain.myTableProvider;
        allMyStoreables = new TreeMap<>();
        sessionChanges = new TreeMap<>();
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
        MyStoreable s = (MyStoreable) sessionChanges.put(key, value);
        if (s != null) {
            return s;
        } else {
            numberOfUnsavedChanges++;
            numberOfElements++;
            return null;
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        MyStoreable s = (MyStoreable) sessionChanges.remove(key);
        if (s != null) {
            numberOfUnsavedChanges++;
            numberOfElements--;
            return s;
        } else {
            return null;
        }
    }

    public int size() {
        return numberOfElements;
    }

    @Override
    public List<String> list() {
        Set<String> keySet = sessionChanges.keySet();
        List<String> list = keySet.stream().collect(Collectors.toCollection(() -> new LinkedList()));
        return list;
    }

    public int commit() throws IOException {
        String key;
        MyStoreable value;
        rm();
        writeSignature();
        try {
            for (Map.Entry<String, Storeable> i : sessionChanges.entrySet()) {
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
                try (DataOutputStream outStream = new DataOutputStream(
                            new FileOutputStream(pathToFile, true))) {
                    writeValue(outStream, key, value);
                    outStream.close();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        allMyStoreables = new TreeMap<>(sessionChanges);
        int n = numberOfUnsavedChanges;
        numberOfUnsavedChanges = 0;
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
        try {
            File[] dirs = currentTableFile.listFiles();
            assert dirs != null;
            for (File dir : dirs) {
                if (!dir.isDirectory() && !dir.getName().equals("signature.tsv")) {
                    System.err.println(dir.getName()
                            + " is not directory");
                    System.exit(-1);
                }
                if (!dir.getName().equals("signature.tsv")) {
                    File[] dats = dir.listFiles();
                    assert dats != null;
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
        sessionChanges = new TreeMap<>(allMyStoreables);
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
        sessionChanges = new HashMap<>(allMyStoreables);
        int temp = numberOfUnsavedChanges;
        numberOfUnsavedChanges = 0;
        numberOfElements = allMyStoreables.size();
        return temp;
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
            throw new IllegalArgumentException("MyTable.get: Null key");
        }
        return sessionChanges.get(key);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return numberOfUnsavedChanges;
    }
}
