package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.Parser;
import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database implements TableProvider {
    private Map<String, Table> tables;
    private Path databasePath;

    public Database(String dbPath) {
        try {
            databasePath = Paths.get(dbPath);
            if (!Files.exists(databasePath)) {
                Files.createDirectory(databasePath);
            }
            if (!Files.isDirectory(databasePath)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalArgumentException("Cannot create database here: " + databasePath.toString(), e);
        }

        tables = new HashMap<>();
        try (DirectoryStream<Path> directory = Files.newDirectoryStream(databasePath)) {
            for (Path entry : directory) {
                if (!Files.isDirectory(entry)) {
                    throw new ContainsWrongFilesException(databasePath.toString());
                }

                Table currentTable = new MultiFileTable(databasePath, entry.getFileName().toString(), this);
                tables.put(entry.getFileName().toString(), currentTable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Table getTable(String name) {
        if (!isNameCorrect(name)) {
            throw new InputMistakeException("Illegal table name: " + name);
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        try {
            if (getTable(name) != null) {
                return null;
            }

            Path tablePath = databasePath.resolve(name);
            if (!Files.exists(tablePath)) {
                try {
                    Files.createDirectory(tablePath);
                } catch (IOException | SecurityException e) {
                    throw new IOException("Cannot create " + tablePath.getFileName());
                }
            }

            if (columnTypes == null || columnTypes.isEmpty()) {
                throw new IllegalArgumentException("Wrong columnTypes format");
            }
            try (FileWriter file = new FileWriter(tablePath.resolve(Serializer.SIGNATURE_FILE_NAME).toFile())) {
                StringBuilder types = new StringBuilder();
                for (Class<?> entry : columnTypes) {
                    String type = Serializer.TYPES_TO_NAMES.get(entry);
                    if (type == null) {
                        throw new IllegalArgumentException("Unsupported type " + entry);
                    }
                    types.append(type);
                    types.append(" ");
                }
                types.deleteCharAt(types.length() - 1);
                file.write(types.toString());
            } catch (IOException e) {
                throw new IOException("Cannot write table signature", e);
            }

            Table newTable = new MultiFileTable(databasePath, name, this);
            tables.put(name, newTable);
            return newTable;
        } catch (IOException e) {
            throw new InputMistakeException("Cannot create table: " + e.getMessage());
        }
    }

    @Override
    public void removeTable(String name) {
        if (getTable(name) == null) {
            throw new TableDoesNotExistException(name);
        }

        MultiFileTable table = (MultiFileTable) tables.get(name);
        table.destroy();

        tables.remove(name);
        try {
            Utils.rm(databasePath.resolve(name));
        } catch (IOException e) {
            throw new RuntimeException("Cannot remove directory (" + name + ") from disk: " + e.getMessage(), e);
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null || value == null) {
            throw new NullArgumentException("deserialize");
        }

        value = value.trim();
        int length = value.length();
        if (length < 3 || value.charAt(0) != '[' || value.charAt(length - 1) != ']') {
            throw new JSONParseException("wrong json format");
        }

        value = value.substring(1, length - 1); // Delete brackets
        Parser parser = new Parser(value, ',', '\"');
        Storeable storeable = createFor(table);

        Serializer.deserialize(table, storeable, parser);
        return storeable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return "[" + Serializer.serialize(table, value, ',', '\"') + "]";
    }

    @Override
    public Storeable createFor(Table table) {
        MultiFileTable databaseTable = (MultiFileTable) table;
        return new TableEntry(databaseTable.signature);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        MultiFileTable databaseTable = (MultiFileTable) table;
        Storeable storeable = new TableEntry(databaseTable.signature);

        if (databaseTable.signature.size() != values.size()) {
            throw new IndexOutOfBoundsException(
                    "Expected: " + databaseTable.signature.size() + ", found: " + values.size());
        }

        for (int i = 0; i < values.size(); ++i) {
            storeable.setColumnAt(i, values.get(i));
        }

        return storeable;
    }

    @Override
    public List<String> getTableNames() {
        List<String> list = new LinkedList<>();
        list.addAll(tables.keySet());
        return list;
    }

    private boolean isNameCorrect(String name) {
        return (name != null) && !(name.matches(".*[</\"*%|\\\\:?>].*|.*\\."));
    }
}
