package ru.fizteh.fivt.students.olga_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PvTable implements TableProvider {
    private static final int N = 16;
    public static String path;

    public PvTable(String p) {
        path = p;
    }
    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    //@Override
    public oTable getTable(String name) {
        oTable t = new oTable(name, JUnit.path);
        try {
            File[] dirs = t.table.listFiles();
            for (File dir : dirs) {
                if (!dir.isDirectory()) {
                    try {
                        throw new Exception(dir.getName()
                                + " is not directory");
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(-1);
                    }
                }
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
                    String value;
                    RandomAccessFile file
                            = new RandomAccessFile(dat.getAbsolutePath(), "r");
                    boolean end = false;
                    while (!end) {
                        try {
                            int length = file.readInt();
                            byte[] bytes = new byte[length];
                            file.readFully(bytes);
                            key = new String(bytes, "UTF-8");
                            length = file.readInt();
                            bytes = new byte[length];
                            file.readFully(bytes);
                            value = new String(bytes, "UTF-8");
                            t.numberOfElements++;
                            t.map.put(key, value);
                            if (!(nDirectory == Math.abs(key.getBytes("UTF-8")[0] % N))
                                    || !(nFile == Math.abs((key.getBytes("UTF-8")[0] / N) % N))) {
                                System.err.println("Error while reading table " + t.tableName);
                                System.exit(-1);
                            }
                        } catch (IOException e) {
                            end = true;
                        }
                    }
                    file.close();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return t;
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Directory name is null");
            }
        String pathToTable = JUnit.path + File.separator + name;
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            return null;
        } else {
            Table tab = new oTable(name, path);
            table.mkdir();
            return tab;
        }
    }

    /**
     * Удаляет таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     */
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name table is null");
        }
        String pathToTable = JUnit.path + File.separator + name;
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            throw new IllegalStateException();
        } else {
            if (JUnit.currentTable != null) {
                if (JUnit.currentTable.getName().equals(name)) {
                    JUnit.currentTable = null;
                }
            }
            JUnit.tableList.remove(name);
            oTable t = new oTable(name, JUnit.path);
            t.rm();
            table.delete();
        }
    }
}
