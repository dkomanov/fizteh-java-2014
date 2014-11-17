package ru.fizteh.fivt.students.alina_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.util.*;

public class BdTable implements Table {
    public String tableName;
    public String path;
    public File table;
    public Map<Integer, TableState> tableStates;
    public Map<String, String> fm;
    public int numberOfElements;
    private static final int NUMBER_OF_DIR = 16;
    private static final int NUMBER_OF_FILE = 16;
    private static final String ENCODING = "UTF-8";
    public int unsavedChangesCounter;
    public int numOfState;

    public BdTable(String name, String pathname) {
        fm = new TreeMap<>();
        tableStates = new TreeMap<>();
        path = pathname + File.separator + name;
        table = new File(path);
        tableName = name;
        TableState ts = new TableState(fm, unsavedChangesCounter, numberOfElements);
        tableStates.put(numOfState, ts);
    }

    @Override
    public String getName() {
        return tableName;
    };

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        String s = fm.get(key);
        return s;
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
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        String s = fm.put(key, value);
        if (s != null) {
            return s;
        } else {
            unsavedChangesCounter++;
            numberOfElements++;
            return null;
        }
    };

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
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        String s = fm.remove(key);
        if (s != null) {
            unsavedChangesCounter++;
            numberOfElements--;
            return s;
        } else {
            return null;
        }
    }

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        return numberOfElements;
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    @Override
    public int commit() {
        rm();
        try {
            for (Map.Entry<String, String> i : fm.entrySet()) {
                String key;
                String value;
                key = i.getKey();
                value = i.getValue();
                Integer ndirectory = Math.abs(key.getBytes(ENCODING)[0] % NUMBER_OF_DIR);
                Integer nfile = Math.abs((key.getBytes(ENCODING)[0] / NUMBER_OF_FILE) % NUMBER_OF_FILE);
                String pathToDir = path + File.separator + ndirectory.toString()
                        + ".dir";
                //System.out.println(pathToDir);
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
                DataOutputStream outStream = new DataOutputStream(
                        new FileOutputStream(pathToFile, true));
                writeValue(outStream, key);
                writeValue(outStream, value);
                outStream.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        TableState ts = new TableState(fm, unsavedChangesCounter, numberOfElements);
        tableStates.put(++numOfState, ts);
        int n = unsavedChangesCounter;
        unsavedChangesCounter = 0;
        return n;
    }
    private void writeValue(DataOutputStream outStream, String key) throws IOException {
        byte[] byteWord = key.getBytes(ENCODING);
        outStream.writeInt(byteWord.length);
        outStream.write(byteWord);
        outStream.flush();
    }

    public void rm() {

        File[] dirs = this.table.listFiles();
        if (dirs != null) {
            for (File dir : dirs) {
                if (!dir.isDirectory()) {
                    System.err.println(dir.getName()
                            + " is not directory");
                    System.exit(-1);
                }
                File[] dats = dir.listFiles();
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

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    @Override
    public int rollback() {
        TableState ts = tableStates.get(numOfState);
        fm = new HashMap<>(ts.fm);
        numberOfElements = ts.numberOfElements;
        int n = unsavedChangesCounter;
        unsavedChangesCounter = ts.unsavedChangesCounter;
        return n;
    }

    /**
     * Выводит список ключей таблицы
     *
     * @return Список ключей.
     */
    @Override
    public List<String> list() {
        Set<String> keySet = fm.keySet();
        List<String> list = new LinkedList();
        for (String current : keySet) {
            list.add(current);
        }
        return list;
    }
}
