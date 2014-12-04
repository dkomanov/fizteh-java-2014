package ru.fizteh.fivt.students.deserg.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

/**
 * Created by deserg on 20.10.14.
 */
public class DbTableProvider implements TableProvider {

    private Map<String, DbTable> tables = new HashMap<>();
    private Set<String> removedTables = new HashSet<>();
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

        if (currentTable != null && currentTable.getNumberOfUncommittedChanges() > 0) {
            System.out.println(currentTable.getNumberOfUncommittedChanges() + " unsaved changes");
            return;
        }

        currentTable = tables.get(name);
        System.out.println("using " + name);
    }


    /**
     * Возвращает таблицу с указанным названием.
     *
     * Последовательные вызовы метода с одинаковыми аргументами должны возвращать один и тот же объект таблицы,
     * если он не был удален с помощью {@link #removeTable(String)}.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     *
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


        if (removedTables.contains(name)) {
            throw new IllegalStateException("Database \"" + fileName + "\": getTable: table was removed");
        } else {
            return tables.get(name);
        }
    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException При ошибках ввода/вывода.
     */
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {

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
            removedTables.remove(name);
            return table;
        }

    }


    /**
     * Удаляет существующую таблицу с указанным названием.
     *
     * Объект удаленной таблицы, если был кем-то взят с помощью {@link #getTable(String)},
     * с этого момента должен бросать {@link IllegalStateException}.
     *
     * @param name Название таблицы.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     * @throws java.io.IOException - при ошибках ввода/вывода.
     */
    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": removeTable: null Table name");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Database \"" + dbPath + "\": removeTable: empty Table name");
        }

        String fileName = Paths.get("").resolve(name).getFileName().toString();

        if (!Shell.checkName(fileName)) {
            throw new IllegalArgumentException("Database \"" + fileName + "\": removeTable: unacceptable table name");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException("Database \"" + dbPath + "\": removeTable: table does not exist");
        }

        removedTables.add(name);
        tables.remove(name);
    }




    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     *
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    Storeable deserialize(Table table, String value) throws ParseException;

    /**
     * Преобразовывает объект {@link Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link Storeable}.
     * @param value {@link Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в {@link Storeable} и типа колонки в таблице.
     */
    String serialize(Table table, Storeable value) throws ColumnFormatException;

    /**
     * Создает новый пустой {@link Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link Storeable}.
     * @return Пустой {@link Storeable}, нацеленный на использование с этой таблицей.
     */
    Storeable createFor(Table table);

    /**
     * Создает новый {@link Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table Таблица, которой должен принадлежать {@link Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link Storeable}, проинициализированный переданными значениями.
     * @throws ColumnFormatException При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException При несоответствии числа переданных значений и числа колонок.
     */
    Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException;

    /**
     * Возвращает имена существующих таблиц, которые могут быть получены с помощью {@link #getTable(String)}.
     *
     * @return Имена существующих таблиц.
     */
    List<String> getTableNames();
    /**
     * Not-interface methods begin here
     */
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
