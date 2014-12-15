package ru.fizteh.fivt.students.olga_chupakhina.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTableProvider implements TableProvider {

    public static String path;
    public static Map<String, Table> tableList;
    public static OTable currentTable;
    private Serializer ts = new Serializer();

    public OTableProvider(String p) {
        if (p == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        path = p;
        tableList = new HashMap<String, Table>();
        currentTable = null;
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
            throw new IllegalArgumentException("Directory name is null");
        }
        return StoreableMain.tableProvider.tableList.get(name);
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
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("Directory name or types of columns is null");
        }
        String pathToTable = path + File.separator + name;
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            return null;
        } else {
            Table t = new OTable(name, path, columnTypes);
            if (!table.mkdir()) {
                System.err.println("Unable to create a table");
            }
            return t;
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
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Name table is null");
        }
        String pathToTable = StoreableMain.tableProvider.path + File.separator + name;
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            throw new IllegalStateException("Table not exist");
        } else {
            if (StoreableMain.tableProvider.currentTable != null) {
                if (StoreableMain.tableProvider.currentTable.getName().equals(name)) {
                    StoreableMain.tableProvider.currentTable = null;
                }
            }
            OTable t = (OTable) StoreableMain.tableProvider.tableList.get(name);
            StoreableMain.tableProvider.tableList.remove(name);
            t.rm();
            table.delete();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        //ts = new TableSerializer();
        return ts.deserialize(table, value, ((OTable) table).signature);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        ts = new Serializer();
        return ts.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        List<Object> lo = new ArrayList<Object>();
        return new OStoreable(lo, ((OTable) table).signature);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Object> lo = new ArrayList<Object>(values);
        return new OStoreable(lo, ((OTable) table).signature);
    }

    @Override
    public List<String> getTableNames() {
        List<String> tablesNames = new ArrayList<String>(tableList.keySet());
        return tablesNames;
    }
}