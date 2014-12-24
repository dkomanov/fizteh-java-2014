package ru.fizteh.fivt.students.kinanAlsarmini.storable.database;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.MultifileMapUtils;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.StoreableUtils;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.TypesFormatter;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.xml.XmlDeserializer;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.xml.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.AbstractMap;

public class DatabaseTableProvider implements TableProvider {
    static final String SIGNATURE_FILE = "signature.tsv";
    private static final String CHECK_EXPRESSION = "[0-9A-Za-zА-Яа-я]+";

    HashMap<String, DatabaseTable> tables = new HashMap<String, DatabaseTable>();
    private String databaseDirectoryPath;
    private DatabaseTable activeTable = null;

    public DatabaseTableProvider(String databaseDirectoryPath) {
        if (databaseDirectoryPath == null) {
            throw new IllegalArgumentException("database directory cannot be null");
        }

        this.databaseDirectoryPath = databaseDirectoryPath;

        File databaseDirectory = new File(databaseDirectoryPath);

        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("set database directory, not file");
        }

        for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }

            List<Class<?>> columnTypes = readTableSignature(tableFile.getName());

            if (columnTypes == null) {
                throw new IllegalArgumentException("table directory is empty");
            }

            DatabaseTable table = new DatabaseTable(this, databaseDirectoryPath, tableFile.getName(), columnTypes);

            tables.put(table.getName(), table);
        }
    }

    public AbstractMap<String, Integer> getTables() {
        HashMap<String, Integer> shownTables = new HashMap<String, Integer>();

        for (Map.Entry<String, DatabaseTable> table : tables.entrySet()) {
            shownTables.put(table.getKey(), table.getValue().size());
        }

        return shownTables;
    }

    @Override
    public Table getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        checkTableName(name);

        DatabaseTable table = tables.get(name);

        if (table == null) {
            return null;
        }

        if (activeTable != null && activeTable.getUncommittedChangesCount() > 0) {
            throw new IllegalStateException(String.format("%d unsaved changes",
                        activeTable.getUncommittedChangesCount()));
        }

        activeTable = table;

        return table;
    }

    @Override
    public List<String> getTableNames() {
        return new ArrayList(tables.keySet());
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        checkTableName(name);

        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types cannot be null");
        }

        checkColumnTypes(columnTypes);

        if (tables.containsKey(name)) {
            return null;
        }

        DatabaseTable table = new DatabaseTable(this, databaseDirectoryPath, name, columnTypes);

        tables.put(name, table);

        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table's name cannot be null");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(String.format("%s not exists", name));
        }

        tables.remove(name);

        File tableFile = new File(databaseDirectoryPath, name);

        MultifileMapUtils.deleteFile(tableFile);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }

        XmlDeserializer deserializer = new XmlDeserializer(value);
        Storeable result = null;
        List<Object> values = new ArrayList<>(table.getColumnsCount());

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            try {
                Class<?> expectedType = table.getColumnType(index);
                Object columnValue = deserializer.getNext(expectedType);

                StoreableUtils.checkValue(columnValue, expectedType);
                values.add(columnValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("incompatible type: " + e.getMessage(), index);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Xml representation doesn't match the format", index);
            }
        }
        try {
            deserializer.close();
            result = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("incompatible types: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Xml representation doesn't match the format", 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return result;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        try {
            XmlSerializer xmlSerializer = new XmlSerializer();

            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(value.getColumnAt(index));
            }

            xmlSerializer.close();

            return xmlSerializer.getRepresentation();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("incorrect value");
        }
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return rawCreateFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null");
        }

        DatabaseRow row = rawCreateFor(table);
        row.setColumns(values);

        return row;
    }

    private List<Class<?>> readTableSignature(String tableName) {
        File tableDirectory = new File(databaseDirectoryPath, tableName);
        File signatureFile = new File(tableDirectory, SIGNATURE_FILE);
        String signature = null;

        if (!signatureFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(signatureFile))) {
            signature = reader.readLine();
        } catch (IOException e) {
            System.err.println("error loading signature file: " + e.getMessage());
            return null;
        }

        if (signature == null) {
            throw new IllegalArgumentException("incorrect signature file");
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();

        for (final String columnType : signature.split("\\s+")) {
            Class<?> type = TypesFormatter.getTypeByName(columnType);

            if (type == null) {
                throw new IllegalArgumentException("unknown type");
            }

            columnTypes.add(type);
        }
        return columnTypes;
    }

    private boolean checkCorrectTable(File tableDirectory) {
        File signatureFile = new File(tableDirectory, SIGNATURE_FILE);

        return signatureFile.exists();
    }

    private DatabaseRow rawCreateFor(Table table) {
        DatabaseRow row = new DatabaseRow();

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            row.addColumn(table.getColumnType(index));
        }

        return row;
    }

    private void checkColumnTypes(List<Class<?>> columnTypes) {
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException("unknown column type");
            }

            TypesFormatter.getSimpleName(columnType);
        }
    }

    private void checkTableName(String name) {
        if (!name.matches(CHECK_EXPRESSION)) {
            throw new IllegalArgumentException("Bad symbol!");
        }
    }
}
