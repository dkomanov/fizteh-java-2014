package ru.fizteh.fivt.students.olga_chupakhina.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static ru.fizteh.fivt.students.olga_chupakhina.storeable.Serializer.classToString;
import static ru.fizteh.fivt.students.olga_chupakhina.storeable.Serializer.stringToClass;

public class OTable implements Table {

    public TableProvider tableProvider;
    public List<Class<?>> signature;
    private Map<String, Storeable> allOStoreables;
    public int unsavedChangesCounter;
    public String tableName;
    public String path;
    private File table;
    public int numberOfElements;
    public int columnsCount;
    final ThreadLocal<Map<String, Storeable>> sessionChanges =
            ThreadLocal.withInitial(HashMap::new);
    private ReentrantReadWriteLock tableOperationsLock = new ReentrantReadWriteLock(true);

    public OTable(String name, String pathname, List<Class<?>> columnTypes) {
        tableProvider = StoreableMain.tableProvider;
        allOStoreables = new TreeMap<>();
        path = pathname + File.separator + name;
        table = new File(path);
        tableName = name;
        unsavedChangesCounter = 0;
        if (columnTypes != null) {
            signature = new ArrayList<Class<?>>(columnTypes);
            columnsCount = signature.size();
        } else {
            signature = new ArrayList<Class<?>>();
        }
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ для нового значения. Не может быть null.
     * @param value Новое значение. Не может быть null.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - при попытке передать Storeable с колонками другого типа.
     */
    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        tableOperationsLock.readLock().lock();
        try {
            Storeable s = sessionChanges.get().put(key, value);
            if (s != null) {
                return s;
            } else {
                unsavedChangesCounter++;
                numberOfElements++;
                return null;
            }
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Предыдущее значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        tableOperationsLock.readLock().lock();
        try {
            OStoreable s = (OStoreable) sessionChanges.get().remove(key);
            if (s != null) {
                unsavedChangesCounter++;
                numberOfElements--;
                return s;
            } else {
                return null;
            }
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }

    /**
     * Возвращает количество ключей в таблице. Возвращает размер текущей версии, с учётом незафиксированных изменений.
     *
     * @return Количество ключей в таблице.
     */
    public int size() {
        return numberOfElements;
    }

    @Override
    public List<String> list() {
        tableOperationsLock.readLock().lock();
        try {
            Set<String> keySet = sessionChanges.get().keySet();
            return new LinkedList(keySet);
        } finally {
            tableOperationsLock.readLock().unlock();
        }
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Число записанных изменений.
     *
     * @throws java.io.IOException если произошла ошибка ввода/вывода. Целостность таблицы не гарантируется.
     */
    public int commit() throws IOException {
        int n;
        tableOperationsLock.readLock().lock();
        try {
            String key;
            OStoreable value;
            rm();
            writeSignature();
            try {
                for (Map.Entry<String, Storeable> i : sessionChanges.get().entrySet()) {
                    key = i.getKey();
                    value = (OStoreable) i.getValue();
                    Integer ndirectory = Math.abs(key.getBytes("UTF-8")[0] % 16);
                    Integer nfile = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
                    String pathToDir = path + File.separator + ndirectory.toString()
                            + ".dir";
                    File file = new File(pathToDir);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String pathToFile = path + File.separator + ndirectory.toString()
                            + ".dir" + File.separator + nfile.toString() + ".dat";
                    //System.out.println(pathToFile);
                    file = new File(pathToFile);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (DataOutputStream outStream = new DataOutputStream(
                            new FileOutputStream(pathToFile, true))) {
                        writeValue(outStream, key, value);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
            allOStoreables = new TreeMap<>(sessionChanges.get());
            n = unsavedChangesCounter;
            unsavedChangesCounter = 0;
        } finally {
            tableOperationsLock.readLock().unlock();
        }
        return n;
    }

    public void writeValue(DataOutputStream os, String key, OStoreable value) throws IOException {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] valueBytes = tableProvider.serialize(this, value).getBytes("UTF-8");
        os.writeInt(keyBytes.length);
        os.write(keyBytes);
        os.writeInt(valueBytes.length);
        os.write(valueBytes);
    }

    public void rm() {
        File[] dirs = this.table.listFiles();
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
                            System.out.println("Error while reading table " + tableName);
                        }


                    }

                    if (!dir.delete()) {
                        System.out.println("Error while reading table " + tableName);
                    }
                }
            }
        }
    }

    public void load() throws Exception {
        if (!table.exists()) {
            throw new NullPointerException("Directory name is null");
        }
        readSignature();
        tableOperationsLock.readLock().lock();
        try {
            try {
                File[] dirs = table.listFiles();
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
                                        System.err.println("Error while reading table " + tableName);
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
            tableOperationsLock.readLock().unlock();
        }
        sessionChanges.set(allOStoreables);
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
            value = tableProvider.deserialize(this, new String(valueBytes, "UTF-8"));
            allOStoreables.put(key, value);
            return key;
        } catch (ColumnFormatException e) {
            throw new ColumnFormatException("database: JSON structure is invalid");
        }
    }

    public void writeSignature() throws IOException {
        PrintWriter out = new PrintWriter(table.toPath().resolve("signature.tsv").toString());
        for (Class<?> type : signature) {
            out.print(classToString(type));
            out.print("\t");
        }
        columnsCount = signature.size();
        out.close();
    }

    private void readSignature() throws IOException {
        signature.clear();
        try (BufferedReader reader = Files.newBufferedReader(table.toPath().resolve("signature.tsv"))) {
            String line = reader.readLine();
            for (String token : line.split("\t")) {
                signature.add(stringToClass(token));
            }
        } catch (Exception e) {
            throw new IOException(tableName + ": No signature file or it's empty");
        }
        columnsCount = signature.size();
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Число откаченных изменений.
     */
    public int rollback() {
        int n;
        tableOperationsLock.readLock().lock();
        try {
            sessionChanges.set(allOStoreables);
            n = unsavedChangesCounter;
            unsavedChangesCounter = 0;
            numberOfElements = allOStoreables.size();
        } finally {
            tableOperationsLock.readLock().unlock();
        }
        return n;
    }

    /**
     * Возвращает количество колонок в таблице.
     *
     * @return Количество колонок в таблице.
     */
    @Override
    public int getColumnsCount() {
        return columnsCount;
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        OStoreable s;
        tableOperationsLock.readLock().lock();
        try {
            if (key == null) {
                throw new IllegalArgumentException("Key is a null-string");
            }
            s = (OStoreable) sessionChanges.get().get(key);
        }             finally {
            tableOperationsLock.readLock().unlock();
        }
        return s;
    }



    /**
     * Возвращает количество изменений, ожидающих фиксации.
     *
     * @return Количество изменений, ожидающих фиксации.
     */
    @Override
    public int getNumberOfUncommittedChanges() {
        return unsavedChangesCounter;
    }
}
