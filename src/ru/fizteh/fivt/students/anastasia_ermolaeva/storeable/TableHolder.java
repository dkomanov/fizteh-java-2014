package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableHolder implements TableProvider {
    private Path rootPath;
    private Map<String, DBTable> tableMap;

    public TableHolder(final String rootDir) throws IOException {
        try {
            rootPath = Paths.get(rootDir);
            if (!Files.exists(rootPath)) {
                Files.createDirectory(rootPath);
            }
            if (!Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException(rootDir
                        + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir
                    + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        Utility.checkDirectorySubdirectories(rootPath);
        try (DirectoryStream<Path> databaseStream = Files.newDirectoryStream(rootPath)) {
            for (Path tableDirectory : databaseStream) {
                String tableName = tableDirectory.getFileName().toString();
                tableMap.put(tableName, new DBTable(rootPath, tableName, this));
            }
        }
    }

    public final void close() throws IOException {
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().commit();
            }
        }
        tableMap.clear();
    }

    /**
     * Возвращает таблицу с указанным названием.
     * <p>
     * Последовательные вызовы метода с одинаковыми аргументами должны возвращать один и тот же объект таблицы,
     * если он не был удален с помощью {@link #removeTable(String)}.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */

    @Override
    public Table getTable(String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            /*
            * Table was removed.
             */
            if (tableMap.get(tableName) == null) {
                throw new IllegalStateException("Table was removed");
            }
            return tableMap.get(tableName);
        } else {
            return null;
        }
    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name   Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException      При ошибках ввода/вывода.
     */
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        Utility.checkTableName(name);
        Utility.checkTableColumnTypes(columnTypes);
        /*
        * Table doesn't exist or has been removed.
         */
        if (tableMap.get(name) == null) {
            Path pathTableDirectory = rootPath.resolve(name);
            Files.createDirectory(pathTableDirectory);
            DBTable newTable = new DBTable(rootPath,
                    name, new HashMap<>(), columnTypes, this);
            Path tableSignaturePath = pathTableDirectory.resolve(Utility.TABLE_SIGNATURE);
            Files.createFile(tableSignaturePath);

            try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
                for (Class type : columnTypes) {
                    String s = Utility.wrappersToPrimitive.get(type) + " ";
                    writeSig.write(s.getBytes(Utility.ENCODING));
                }
            }
            tableMap.put(name, newTable);
            return newTable;
        } else {
            return null;
        }
    }

    /**
     * Удаляет существующую таблицу с указанным названием.
     * <p>
     * Объект удаленной таблицы, если был кем-то взят с помощью {@link #getTable(String)},
     * с этого момента должен бросать {@link IllegalStateException}.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException    Если таблицы с указанным названием не существует.
     * @throws java.io.IOException      - при ошибках ввода/вывода.
     */
    @Override
    public void removeTable(String name) throws IOException {
        Utility.checkTableName(name);
        if (tableMap.get(name) == null) {
            throw new IllegalStateException(name + " doesn't exist");
        } else {
            Path tableDirectory = tableMap.get(name).getDBTablePath();
            Utility.recursiveDeleteCopy(tableDirectory);
            tableMap.put(name, null);
        }
    }

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<String> values = Utility.parseString(value);
        List<Object> tableValues = Utility.formatStringValues(table, values);
        try {
            return createFor(table, tableValues);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Преобразовывает объект {@link ru.fizteh.fivt.storage.structured.Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value {@link ru.fizteh.fivt.storage.structured.Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в {@link ru.fizteh.fivt.storage.structured.Storeable} и типа колонки в таблице.
     */
    @Override
    public String serialize(Table table, Storeable value) {
        List<String> storeableValues = Utility.getStoreableValues(table, value);
        String joined = String.join(Utility.FORMATTER, storeableValues);
        StringBuilder result = new StringBuilder();
        result.append(Utility.VALUE_START_LIMITER);
        result.append(joined);
        result.append(Utility.VALUE_END_LIMITER);
        return result.toString();
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    @Override
    public Storeable createFor(Table table) {
        int size = table.getColumnsCount();
        List<Class<?>> requiredTypes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            requiredTypes.add(table.getColumnType(i));
        }
        return new Record(requiredTypes);
    }

    /**
     * Создает новый {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table  Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link ru.fizteh.fivt.storage.structured.Storeable}, проинициализированный переданными значениями.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException                               При несоответствии числа переданных значений и числа колонок.
     */
    @Override
    public Storeable createFor(Table table, List<?> values) {
        Storeable aimedRecord = createFor(table);
        int size = table.getColumnsCount();
        if (values.size() != size) {
            throw new IndexOutOfBoundsException("wrong amount of columns");
        } else {
            for (int i = 0; i < size; i++) {
                Object currentValue = values.get(i);
                aimedRecord.setColumnAt(i, currentValue);
            }
            return aimedRecord;
        }
    }

    /**
     * Возвращает имена существующих таблиц, которые могут быть получены с помощью {@link #getTable(String)}.
     *
     * @return Имена существующих таблиц.
     */
    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            if (!(entry.getValue() == null)) {
                tableNames.add(entry.getKey());
            }
        }
        return tableNames;
    }
}
