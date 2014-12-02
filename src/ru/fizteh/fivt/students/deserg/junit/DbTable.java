package ru.fizteh.fivt.students.deserg.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by deserg on 04.10.14.
 */
public class DbTable implements Table {

    private Map<String, String> commitedData = new HashMap<>();
    private Map<String, String> addedData = new HashMap<>();
    private Map<String, String> removedData = new HashMap<>();
    private Map<String, String> changedData = new HashMap<>();
    private String tableName;
    private Path tablePath;

    public DbTable(Path path) {
        tablePath = path;
        tableName = path.getFileName().toString();
    }

    /**
     * Возвращает название базы данных.
     */
    @Override
    public String getName() {
        return tableName;
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public String get(String key) throws IllegalArgumentException {

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": get: null key");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": get: empty key");
        }


        String value = addedData.get(key);
        if (value != null) {
            return value;
        }

        value = changedData.get(key);
        if (value != null) {
            return value;
        }

        value = commitedData.get(key);
        if (value != null) {
            return value;
        }

        return null;
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ.
     * @param value Значение.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     */
    @Override
    public String put(String key, String value) {

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: null key");
        }

        if (value == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: null value");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: empty key");
        }

        if (commitedData.containsKey(key)) {

            if (removedData.containsKey(key)) {
                removedData.remove(key);
                if (!commitedData.get(key).equals(value)) {
                    changedData.put(key, value);
                }
                return null;
            } else if (changedData.containsKey(key)) {

                if (commitedData.get(key).equals(value)) {
                    return changedData.remove(key);
                } else {
                    return changedData.put(key, value);
                }
            } else {

                changedData.put(key, value);
                return commitedData.get(key);

            }

        } else {
            return addedData.put(key, value);
        }

    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public String remove(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": remove: null key");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": remove: empty key");
        }

        if (addedData.containsKey(key)) {
            return addedData.remove(key);
        }

        if (changedData.containsKey(key)) {
            String value = changedData.get(key);
            changedData.remove(key);
            removedData.put(key, value);
            return value;
        }

        if (removedData.containsKey(key)) {
            return null;
        }

        if (commitedData.containsKey(key)) {
            String value = commitedData.get(key);
            removedData.put(key, value);
            return value;
        }

        return null;
    }

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        return commitedData.size() - removedData.size() + addedData.size();
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    @Override
    public int commit() {

        commitedData.keySet().removeAll(removedData.keySet());
        commitedData.putAll(addedData);
        commitedData.putAll(changedData);

        int changedKeys = changedNum();
        addedData.clear();
        removedData.clear();
        changedData.clear();

        return changedKeys;
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    @Override
    public int rollback() {

        int changedKeys = changedNum();
        addedData.clear();
        removedData.clear();
        changedData.clear();

        return changedKeys;
    }

    /**
     * Выводит список ключей таблицы
     *
     * @return Список ключей.
     */
    @Override
    public List<String> list() {

        List<String> keyList = new LinkedList<>();
        keyList.addAll(commitedData.keySet());
        keyList.addAll(addedData.keySet());

        return keyList;
    }


    public int changedNum() {
        return addedData.size() + removedData.size() + changedData.size();
    }

    private void readKeyValue(Path filePath, int dir, int file) throws MyIOException {

        if (Files.exists(filePath)) {
            try (DataInputStream is = new DataInputStream(Files.newInputStream(filePath))) {

                if (is.available() == 0) {
                    throw new MyIOException("File is empty: " + filePath);
                }

                while (is.available() > 0) {

                    int keyLen = is.readInt();
                    if (is.available() < keyLen) {
                        throw new MyIOException("Wrong key size");
                    }

                    byte[] key = new byte[keyLen];
                    is.read(key, 0, keyLen);


                    int valLen = is.readInt();
                    if (is.available() < valLen) {
                        throw new MyIOException("Wrong value size");
                    }
                    byte[] value = new byte[valLen];
                    is.read(value, 0, valLen);

                    String keyStr = new String(key, "UTF-8");
                    String valueStr = new String(value, "UTF-8");

                    int hashValue = keyStr.hashCode();
                    if (hashValue % 16 != dir || hashValue / 16 % 16 != file) {
                        throw new MyIOException("Wrong key file");
                    }

                    put(keyStr, valueStr);
                }

            } catch (IOException e) {
                throw new MyIOException("Reading from disk failed");
            }


        }
    }

    public void read() throws MyIOException {

        commitedData.clear();

        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                try {
                    readKeyValue(filePath, dir, file);
                } catch (MyException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
            }
        }

    }

    private void writeKeyValue(Path filePath, String keyStr, String valueStr) throws MyIOException {

        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(filePath))) {
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");
            os.writeInt(key.length);
            os.write(key);
            os.writeInt(value.length);
            os.write(value);

        } catch (IOException ex) {
            throw new MyIOException("Writing to dist failed");
        }


    }

    public void write() throws MyIOException {

        if (Files.exists(tablePath)) {
            throw new MyIOException("Directory exists but it should not");
        } else {
            try {
                Files.createDirectory(tablePath);
            } catch (IOException ex) {
                throw new MyIOException("Error has occurred while creating table directory");
            }
        }

        for (HashMap.Entry<String, String> entry : commitedData.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            Path dirPath = tablePath.resolve(dir + ".dir");
            Path filePath = dirPath.resolve(file + ".dat");

            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectory(dirPath);
                } catch (IOException ex) {
                    throw new MyIOException(dirPath + ": unable to create");
                }
            }
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    throw new MyIOException(filePath + ": unable to create");
                }
            }

            writeKeyValue(filePath, key, value);

        }

    }


}
