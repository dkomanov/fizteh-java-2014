package ru.fizteh.fivt.students.deserg.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by deserg on 20.10.14.
 */
public class DbTableProvider implements TableProvider {

    private Map<String, DbTable> tables = new HashMap<>();
    private Path dbPath;
    private DbTable currentTable = null;


    public DbTable getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(String name) {
        if (!tables.containsKey(name)) {
            System.out.println(name + " not exists");
            return;
        }

        if (currentTable != null && currentTable.changedNum() > 0) {
            System.out.println(currentTable.changedNum() + " unsaved changes");
            return;
        }

        currentTable = tables.get(name);
        System.out.println("using " + name);
    }


    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": getTable: null table name");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": getTable: empty table name");
        }

        String fileName = Paths.get("").resolve(name).getFileName().toString();

        if (!Shell.checkName(fileName)) {
            throw new IllegalArgumentException("Database \"" + fileName + "\": getTable: unacceptable table name");
        }


        return tables.get(name);
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
            throw new IllegalArgumentException("Database \"" + dbPath + "\": createTable: null table name");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": createTable:  table name");
        }

        String fileName = Paths.get("").resolve(name).getFileName().toString();

        if (!Shell.checkName(fileName)) {
            throw new IllegalArgumentException("Database \"" + fileName + "\": createTable: unacceptable table name");
        }

        if (tables.containsKey(name)) {
            return null;
        } else {
            DbTable table = new DbTable(dbPath.resolve(name));
            tables.put(name, table);
            return table;
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
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": removeTable: invalid Table name");
        }

        String fileName = Paths.get("").resolve(name).getFileName().toString();

        if (!Shell.checkName(fileName)) {
            throw new IllegalArgumentException("Database \"" + fileName + "\": removeTable: unacceptable table name");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException("Database \"" + dbPath + "\": removeTable: table does not exist");
        }

        tables.remove(name);
    }

    public void showTableSet() {

        System.out.println("table_name row_count");
        for (HashMap.Entry<String, DbTable> entry: tables.entrySet()) {
            DbTable table = entry.getValue();
            System.out.println(table.getName() + " " + table.size());
        }

    }

    public Path getDbPath() {
        return dbPath;
    }


    public DbTableProvider(Path inpPath) {
        dbPath = inpPath;
        read();
    }



    public void read() {

        if (dbPath == null || !Files.exists(dbPath)) {
            return;
        }

        File curDir = new File(dbPath.toString());
        File[] content = curDir.listFiles();

        if (content != null) {
            for (File item: content) {
                if (Files.isDirectory(item.toPath())) {

                    DbTable table = new DbTable(item.toPath());
                    try {
                        table.read();
                    } catch (MyIOException ex) {
                        System.out.println(ex.getMessage());
                        System.exit(1);
                    }
                    tables.put(item.getName(), table);
                }
            }
        }

        currentTable = null;
    }

    public void write() {

        Shell.delete(dbPath);

        for (HashMap.Entry<String, DbTable> entry: tables.entrySet()) {

            try {
                entry.getValue().write();
            } catch (MyIOException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }

        }

    }




}
