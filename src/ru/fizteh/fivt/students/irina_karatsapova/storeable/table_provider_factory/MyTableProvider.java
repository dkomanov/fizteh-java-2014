package ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.TypeTransformer;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.XmlDeserializer;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.XmlSerializer;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

public class MyTableProvider implements TableProvider {
    private static final String SIGNATURE_FILENAME = "signature.tsv";

    private File databaseFile;
    private Map<String, MyTable> tables = new HashMap<>();
    private MyTable currentTable = null;

    MyTableProvider(String dir) throws TableException {
        databaseFile = Utils.toFile(dir);
        load();
    }

    private void load() throws TableException {
        for (File file : databaseFile.listFiles()) {
            String tableName = file.getName();
            Path tablePath = Utils.makePathAbsolute(databaseFile, tableName);
            MyTable table = new MyTable(tablePath.toString(), this);
            tables.put(tableName, table);
        }
    }

    public MyTable currentTable() {
        return currentTable;
    }

    public List<String> getTableNames() {
        Set<String> tableNamesSet = tables.keySet();
        List<String> tableNamesList = new ArrayList<>();
        tableNamesList.addAll(tableNamesSet);
        return tableNamesList;
    }

    public MyTable getTable(String name) {
        Utils.checkNotNull(name);
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    public MyTable createTable(String name, List<Class<?>> columnTypes) throws TableException {
        Utils.checkNotNull(name);
        Utils.checkNotNull(columnTypes);
        currentTable = null;
        MyTable createdTable = null;
        if (!tables.containsKey(name)) {
            File tableFile = checkAndCreateTableFile(name);
            createSignatureFile(tableFile, columnTypes);
            createdTable = new MyTable(tableFile.toString(), this);
            tables.put(name, createdTable);
        }
        return createdTable;
    }

    public void removeTable(String name) throws TableException {
        Utils.checkNotNull(name);
        currentTable = null;
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("The table does not exist");
        } else {
            File tablePath = Paths.get(databaseFile.toString(), name).toFile();
            Utils.rmdirs(tablePath);
            tables.remove(name);
        }
    }

    public Table useTable(String name) {
        currentTable = getTable(name);
        return currentTable;
    }

    public Storeable deserialize(Table table, String stringValue) throws ParseException {
        Utils.checkNotNull(stringValue);
        List<Object> values = new ArrayList<>();
        try {
            XmlDeserializer xmlDeserializer = new XmlDeserializer(stringValue);
            xmlDeserializer.start();
            for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
                Class expectedType = table.getColumnType(columnIndex);
                Object value;
                try {
                    value = xmlDeserializer.getNext(expectedType);
                } catch (ColumnFormatException e) {
                    throw new ColumnFormatException(e.getMessage() + " in column " + columnIndex);
                }
                values.add(value);
            }
            xmlDeserializer.finish();
        } catch (ParseException e) {
            throw new ParseException("Incorrect xml representation: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(e.getMessage()
                                                + ", there should be " + table.getColumnsCount() + " columns");
        }
        return createFor(table, values);
    }

    public String serialize(Table table, Storeable tableRawValue)
                            throws ColumnFormatException, IndexOutOfBoundsException, TableException {
        Utils.checkNotNull(tableRawValue);
        try {
            XmlSerializer xmlSerializer = new XmlSerializer();
            xmlSerializer.start();
            TableUtils.checkColumnsFormat(table, tableRawValue);
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(tableRawValue.getColumnAt(index));
            }
            xmlSerializer.finish();
            return xmlSerializer.getResult();
        } catch (IOException e) {
            throw new TableException("Serialization: " + e.getMessage());
        }
    }

    public Storeable createFor(Table table) {
        List<Class<?>> types = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            types.add(table.getColumnType(columnIndex));
        }
        return new MyTableRaw(types);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("wrong number of values in the list: " + values.size()
                    + " (table contains " + table.getColumnsCount() + "columns)");
        }
        Storeable tableRaw = createFor(table);
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            tableRaw.setColumnAt(columnIndex, values.get(columnIndex));
        }
        return tableRaw;
    }

    private File checkAndCreateTableFile(String name) throws TableException {
        File file;
        try {
            file = Utils.makePathAbsolute(databaseFile, name).toFile();
        } catch (InvalidPathException e) {
            throw new TableException("init: Incorrect name");
        }
        if (!file.getName().equals(name)) {
            throw new TableException("init: Incorrect name");
        }
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new TableException("init: Incorrect name");
            }
        }
        if (!file.isDirectory()) {
            throw new TableException("init: Incorrect name: Main directory is a file");
        }
        return file;
    }

    private void createSignatureFile(File tableFile, List<Class<?>> columnTypes) {
        File signatureFile = Utils.makePathAbsolute(tableFile, SIGNATURE_FILENAME).toFile();
        try {
            signatureFile.createNewFile();
        } catch (IOException e) {
            throw new TableException("Can't create signature file");
        }
        try (PrintWriter bufWriter = new PrintWriter(new BufferedWriter(new FileWriter(signatureFile)))) {
            List<String> typeNames = new ArrayList<>();
            for (Class<?> type: columnTypes) {
                String typeName = TypeTransformer.getStringByType(type);
                typeNames.add(typeName);
            }
            bufWriter.print(String.join(" ", typeNames));
            bufWriter.flush();
        } catch (FileNotFoundException e) {
            throw new TableException("That is strange, but signature file does not exist");
        } catch (IOException e) {
            throw new TableException("Can't write into the signature file");
        }
    }
}
