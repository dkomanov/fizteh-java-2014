package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Дмитрий on 25.11.2014.
 */
public class StoreableTable implements Table {

    protected static final int FILES_COUNT = 16;
    protected static final int DIRECTORIES_COUNT = 16;
    private final String formatOfDirectory = ".dir";
    private final String formatOfFile = ".dat";
    private final String encoding = "UTF-8";
    private final String signatureFileName = "signature.tsv";

    public Path dbPath;
    public String name;

    private Map<String, Integer> changedFiles = new TreeMap<>();
    private Map<String, Storeable> activeTable = new HashMap<>();
    private Map<String, Storeable> removed = new HashMap<>();
    private Map<String, Storeable> newKey = new HashMap<>();
    private List<Class<?>> signature;
    private StoreableTableProvider provider;


    public Map<String, Integer> getChangedFiles() {
        return changedFiles;
    }

    public Map<String, Storeable> getActiveTable() {
        return activeTable;
    }

    public Map<String, Storeable> getNewKey() {
        return newKey;
    }

    public Map<String, Storeable> getRemoved() {
        return removed;
    }

    /*TYPES = provider.

    static {
        TYPES = new HashMap<>();
        TYPES.put("int", Integer.class);
        TYPES.put("long", Long.class);
        TYPES.put("byte", Byte.class);
        TYPES.put("float", Float.class);
        TYPES.put("double", Double.class);
        TYPES.put("boolean", Boolean.class);
        TYPES.put("String", String.class);
    }*/

    public final Map<String, Class<?>> types;

    private Class<?> classByName(String name) throws IOException {
        if (!types.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return types.get(name);
    }

    public StoreableTable(Path path) throws IOException {
        dbPath = path.normalize();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        types = new StoreableTableProvider().revClassNames;
    }

    public StoreableTable(StoreableTableProvider newProvider,  String dbName,
                          String parentDir, List<Class<?>> newSignature) {
        name = dbName;
        dbPath = new File(parentDir + File.separator + dbName).toPath();
        signature = newSignature;
        provider = newProvider;
        types = provider.revClassNames;
        readDb();
    }

    public void readDb() {
        try {
            name = dbPath.getFileName().toString();
            File dbDir = dbPath.toFile();
            String dbDirPath = dbPath.toString();
            for (String subdirectory : dbDir.list()) {
                String dirPath = dbDirPath + File.separator + subdirectory;
                File dir = new File(dirPath);
                if (signatureFileName.equals(dir.getName())) {
                    continue;
                }
                if (!dir.isDirectory() || !isCorrectName(subdirectory, ".dir") || !isCorrectSubdirectory(dir)) {
                    throw new RuntimeException("Incorrect database directory");
                }
                for (String file : dir.list()) {
                    readFile(new File(dirPath + File.separator + file));
                }
            }
            Scanner fw = new Scanner(new File(dbDirPath + File.separator + signatureFileName));
            String[] newSignature = fw.nextLine().split(" ");
            signature = new ArrayList<>();
            for (String aNewSignature : newSignature) {
                signature.add(classByName(aNewSignature));
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read db from file");
        } catch (NullPointerException e) {
            //ignore
        }
    }

    private boolean isCorrectSubdirectory(File dir) {
        for (String file : dir.list()) {
            if (new File(file).isDirectory() || !isCorrectName(file, ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectName(String name, String suf) {
            if (!name.endsWith(suf)) {
                return false;
            }
            name = name.replace(suf, "");
            int num = Integer.parseInt(name);
            return (0 <= num && num < DIRECTORIES_COUNT);
    }

    private void readFile(File file) throws IOException {
        try {
            try (DataInputStream stream = new DataInputStream(
                    new FileInputStream(file))) {
                while (true) {
                    String key = readToken(stream);
                    String valueStr = readToken(stream);
                    if (key == null || valueStr == null) {
                        break;
                    }
                    Storeable value = provider.deserialize(this, valueStr);
                    checkSignature(value);
                    activeTable.put(key, value);
                }
            }
        } catch (ParseException e) {
            throw new IOException("Invalid db directory");
        }
    }

    private void checkSignature(Storeable storeable) {
        try {
            for (int i = 0; i < signature.size(); ++i) {
                if (storeable.getColumnAt(i) == null) {
                    continue;
                }
                if (!storeable.getColumnAt(i).getClass().equals(signature.get(i))) {
                    throw new ColumnFormatException("Invalid column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Invalid column number");
        }
    }

    private String readToken(DataInputStream stream) throws IOException {
        if (stream.available() == 0) {
            return null;
        }
        int size = stream.readInt();
        byte[] buf = new byte[size];
        stream.readFully(buf);
        return new String(buf, encoding);
    }

    public int countOfCollisionsInFile(Path path) {
        int count = 0;
        try (DataInputStream iStream = new DataInputStream(Files.newInputStream(path))) {
            while (iStream.available() > 0) {
                readToken(iStream);
                readToken(iStream);
                ++count;
            }
        } catch (IOException e) {
            System.err.println("It was exception on creating stream or in reading from file " + path.toString());
            return -1;
        }
        return count;
    }

    @Override
    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException {
        checkArg("key", key);
        checkArg("value", value);
        checkSignature(value);
        if (activeTable.containsKey(key) && ((CurrentStoreable) activeTable.get(key)).getValues().equals(
                                            ((CurrentStoreable) value).getValues())) {
            if (newKey.containsKey(key)) {
                newKey.remove(key);
            }
            return value;
        }
        removed.remove(key);
        return newKey.put(key, value);
    }

    private void checkArg(String name, Object value)
            throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(name + " shouldn't be null");
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!activeTable.containsKey(key) && !newKey.containsKey(key)) {
            return null;
        }
        if (!newKey.containsKey(key)) {
            removed.put(key, activeTable.get(key));
            return activeTable.get(key);
        } else {
            Storeable value = newKey.get(key);
            //removed.put(key, value);
            newKey.remove(key);
            return value;
        }
    }

    @Override
    public int size() {
        return list().size();
    }

    @Override
    public java.util.List<String> list() {
        java.util.List<String> result = newKey.keySet().stream().collect(Collectors.toList());
        result.addAll(
                activeTable.keySet().stream().filter(key -> !newKey.containsKey(key)).collect(Collectors.toList()));
        removed.keySet().forEach(result::remove);
        return result;
    }

    @Override
    public int commit() {
        removed.keySet().forEach(activeTable::remove);
        for (Map.Entry<String, Storeable> entry : newKey.entrySet()) {
            activeTable.put(entry.getKey(), entry.getValue());
        }
        unload(this, getName());
        return remindChanges(true);
    }

    @Override
    public int rollback() {
        return remindChanges(false);
    }

    private int remindChanges(boolean inCommit) {
        int removedCount = 0;
        if (!inCommit) {
            for (String s : removed.keySet()) {
                if (activeTable.containsKey(s)) {
                    ++removedCount;
                }
            }
        } else {
            removedCount = removed.size();
        }
        int count = removedCount + newKey.size();
        removed.clear();
        newKey.clear();
        return count;
    }

    private Path nameOfPath(String nameOfTable, int directory, int file) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(directory + formatOfDirectory + File.separator + file + formatOfFile);
        }
        return dbPath.resolve(nameOfTable + File.separator + directory + formatOfDirectory
            + File.separator + file + formatOfFile);
    }

    private Path nameOfPath(String nameOfTable, int directory) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(directory + formatOfDirectory + File.separator);
        }
        return dbPath.resolve(nameOfTable + File.separator + directory + formatOfDirectory + File.separator);
    }

    public AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>>
                                            whereToSave(String nameOfTable, String value) {
        int hashCode = value.hashCode();
        int d = hashCode % DIRECTORIES_COUNT;
        int f = hashCode / DIRECTORIES_COUNT % FILES_COUNT;
        return new AbstractMap.SimpleEntry<>(
                nameOfPath(nameOfTable, d, f).toString(),
                new AbstractMap.SimpleEntry<>(d, f));
    }

    public void deleteFiles(String nameOfTable, boolean all) {
        try {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (changedFiles.containsKey(nameOfPath(nameOfTable, i, j).toString()) || all) {
                        if (Files.exists(nameOfPath(nameOfTable, i, j))) {
                            Files.delete(nameOfPath(nameOfTable, i, j));
                        }
                        if (Files.exists(nameOfPath(nameOfTable, i))
                                && nameOfPath(nameOfTable, i).toFile().list().length == 0) {
                            Files.delete(nameOfPath(nameOfTable, i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't delete from disk: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void unload(StoreableTable currentTable, String nameOfTable) {
        deleteFiles(nameOfTable, false);
        boolean[] dir = new boolean[DIRECTORIES_COUNT];
        DataOutputStream[][] streams = new DataOutputStream[DIRECTORIES_COUNT][FILES_COUNT];
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (Map.Entry<String, Storeable> entry :  currentTable.activeTable.entrySet()) {
                AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>> pathOfFile =
                        whereToSave("", entry.getKey());
                if (changedFiles.containsKey(pathOfFile.getKey())) {
                    int d = pathOfFile.getValue().getKey();
                    int f = pathOfFile.getValue().getValue();
                    if (streams[d][f] == null) {
                        if (!dir[d]) {
                            if (!Files.exists(nameOfPath("", d))) {
                                Files.createDirectory(nameOfPath("", d));
                            }
                            dir[d] = true;
                        }
                        streams[d][f] = new DataOutputStream(Files.newOutputStream(
                                nameOfPath("", d, f)));
                    }
                    writeToFile(streams[d][f], entry.getKey(), entry.getValue());
                    Integer collisionCount = changedFiles.get(pathOfFile.getKey());
                    if (collisionCount > 0) {
                        --collisionCount;
                        changedFiles.put(pathOfFile.getKey(), collisionCount);
                    } else {
                        changedFiles.remove(pathOfFile.getKey());
                    }
                }
            }
            changedFiles.clear();
        } catch (IOException e) {
            System.err.println("Can't write to disk: " + e.getMessage());
            changedFiles.clear();
            System.exit(-1);
        } finally {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException ignored) {
                            continue;
                        }
                    }
                }
            }
        }
    }

    private void writeToFile(DataOutputStream oStream, String key, Storeable value) throws IOException {
        writeToken(oStream, key);
        String strValue;
        if (provider != null) {
            strValue = provider.serialize(this, value);
        } else {
            strValue = (new StoreableTableProvider(dbPath.toString()).serialize(this, value));
        }
        writeToken(oStream, strValue);
    }

    private void writeToken(DataOutputStream stream, String str) throws IOException {
        byte[] strBytes = str.getBytes(encoding);
        stream.writeInt(strBytes.length);
        stream.write(strBytes);
    }

    public int getNumberOfUncommittedChanges() {
        int count = 0;
        for (String s : removed.keySet()) {
            if (activeTable.containsKey(s)) {
                ++count;
            }
        }
        count += newKey.size();
        return count;
    }

    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key)
            throws IllegalArgumentException {
        checkArg("key", key);
        if (newKey.containsKey(key)) {
            return newKey.get(key);
        }
        if (!activeTable.containsKey(key) || removed.containsKey(key)) {
            return null;
        }
        return activeTable.get(key);
    }
}
