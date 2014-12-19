package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;

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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableHolder implements TableProvider, AutoCloseable {
    private Path rootPath;
    private Map<String, DBTable> tableMap;
    private Table activeTable;
    private ReadWriteLock tableAccessLock = new ReentrantReadWriteLock(true);

    private boolean valid = true;
    private ReadWriteLock validLock = new ReentrantReadWriteLock(true);

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
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            tableAccessLock.readLock().lock();
            try {
                if (tableMap.containsKey(name)) {
                    try {
                        DBTable table = tableMap.get(name);
                        table.checkIfValid();
                        /*
                        Table wasn't closed.
                         */
                        return table;
                    } catch (IllegalStateException e) {
                        /*
                        Table was closed.
                        We try to load it from disk and return to user a new example of Table.
                        Also we replace closed table with this new in the map of tables in this TableProvider.
                         */
                        try {
                            tableMap.put(name, new DBTable(rootPath, name, this));
                        } catch (IOException io) {
                            throw new DatabaseFormatException("Can't load table", io);
                        }
                    }
                    return tableMap.get(name);
                } else {
                    return null;
                }
            } finally {
                tableAccessLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }


    private Table createNewInstanceTable(String name, List<Class<?>> columnTypes) throws IOException {
        Path pathTableDirectory = rootPath.resolve(name);
        Files.createDirectory(pathTableDirectory);
        DBTable newTable = new DBTable(rootPath,
                name, new HashMap<>(), columnTypes, this);
        Path tableSignaturePath = pathTableDirectory.resolve(Utility.TABLE_SIGNATURE);
        Files.createFile(tableSignaturePath);
        try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
            for (Class type : columnTypes) {
                String s = Utility.WRAPPERS_TO_PRIMITIVE.get(type) + " ";
                writeSig.write(s.getBytes(Utility.ENCODING));
            }
        }
        tableMap.put(name, newTable);
        return newTable;
    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name        Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException      При ошибках ввода/вывода.
     */


    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            Utility.checkTableColumnTypes(columnTypes);
            tableAccessLock.writeLock().lock();
            try {
                DBTable table = tableMap.get(name);
                /*
                * Table doesn't exist.
                */
                if (table == null) {
                    return createNewInstanceTable(name, columnTypes);
                } else {
                    try {
                        table.checkIfValid();
                        /*
                        Table exists and wasn't closed.
                         */
                        return null;
                    } catch (IllegalStateException e) {
                        /*
                        Table was closed. We replace closed table with new created
                         */
                        Utility.recursiveDeleteCopy(rootPath.resolve(name));
                        return createNewInstanceTable(name, columnTypes);
                    }
                }
            } finally {
                tableAccessLock.writeLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
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
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkTableName(name);
            tableAccessLock.writeLock().lock();
            try {
                DBTable table = tableMap.get(name);
                /*
                * Table doesn't exist.
                 */
                if (table == null) {
                    throw new IllegalStateException(name + " doesn't exist");
                }
                Path tableDirectory = rootPath.resolve(table.getName());
                /*
                Invalidate table.
                 */
                if (activeTable == table) {
                    activeTable = null;
                }
                table.close();
                Utility.recursiveDeleteCopy(tableDirectory);
                tableMap.remove(name);
            } finally {
                tableAccessLock.writeLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
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
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> values = Utility.parseString(value);
            List<Object> tableValues = Utility.formatStringValues(table, values);
            try {
                return createFor(table, tableValues);
            } catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        } finally {
            validLock.readLock().unlock();
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
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> storeableValues = Utility.getStoreableValues(table, value);
            String joined = String.join(Utility.FORMATTER, storeableValues);
            return String.valueOf(Utility.VALUE_START_LIMITER) + joined + Utility.VALUE_END_LIMITER;
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    @Override
    public Storeable createFor(Table table) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            int size = table.getColumnsCount();
            List<Class<?>> requiredTypes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                requiredTypes.add(table.getColumnType(i));
            }
            return new Record(requiredTypes);
        } finally {
            validLock.readLock().unlock();
        }
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
        validLock.readLock().lock();
        try {
            checkIfValid();
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
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Возвращает имена существующих таблиц, которые могут быть получены с помощью {@link #getTable(String)}.
     *
     * @return Имена существующих таблиц.
     */
    @Override
    public List<String> getTableNames() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            List<String> tableNames = new ArrayList<>();
            tableMap.forEach((String s, DBTable table) -> tableNames.add(s));
            return tableNames;
        } finally {
            validLock.readLock().unlock();
        }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + rootPath.toAbsolutePath().toString() + "]";
    }

    @Override
    public final void close() {
        validLock.writeLock().lock();
        try {
            checkIfValid();
            tableMap.forEach((s, table) -> {
                try {
                    table.close();
                } catch (IllegalStateException e) {
                    /*
                    Table was already closed.
                    No way(and use) to close it again.
                    */
                }
            });
            valid = false;
            tableMap.clear();
        } finally {
            validLock.writeLock().unlock();
        }
    }

    private void checkIfValid() {
        if (!valid) {
            throw new IllegalStateException("TableProvider was closed\n");
        }
    }
}

