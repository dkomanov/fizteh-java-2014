package ru.fizteh.fivt.students.Bulat_Galiev.storeable;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class TabledbProvider implements TableProvider {
    private static Map<String, Table> tableMap;
    private static Path tablesDirPath;
    private static Table currentTable;
    private static Types serializer;

    public TabledbProvider(final String dir) {
        try {
            tablesDirPath = Paths.get(dir);
            if (!Files.exists(tablesDirPath)) {
                tablesDirPath.toFile().mkdir();
            }
            if (!tablesDirPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Incorrect path.");
            }
            currentTable = null;
            tableMap = new HashMap<>();
            serializer = new Types();
            String[] tablesDirlist = tablesDirPath.toFile().list();
            for (String curTableDir : tablesDirlist) {
                Path curTableDirPath = tablesDirPath.resolve(curTableDir);
                if (curTableDirPath.toFile().isDirectory()) {
                    Table curTable = new Tabledb(curTableDirPath, curTableDir,
                            this, null);
                    tableMap.put(curTableDir, curTable);
                } else {
                    throw new IllegalArgumentException(
                            "Directory contains non-directory files.");
                }
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("directory name " + dir
                    + " is incorrect. " + e.getMessage());
        }
    }

    public static void showTables() {
        Set<String> keys = tableMap.keySet();
        for (String current : keys) {
            System.out.println(current + " " + tableMap.get(current).size());
        }
    }

    public static void changeCurTable(final String name) throws IOException {
        try {
            if (name != null && !name.equals("")) {
                tablesDirPath.resolve(name);
                if (currentTable != null) {
                    int diff = ((Tabledb) currentTable)
                            .getNumberOfUncommittedChanges();
                    if (diff != 0) {
                        System.out.println(diff + " unsaved changes");
                        return;
                    }
                }
                Table newTable = tableMap.get(name);
                if (newTable != null) {
                    if (currentTable != null) {
                        currentTable.commit();
                    }
                    currentTable = newTable;
                    System.out.println("using " + name);
                } else {
                    System.err.println(name + " does not exist");
                }
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        }
    }

    public Table createTable(final String name, final List<Class<?>> columnTypes) {
        try {
            if (name != null && !name.equals("")) {
                if (tableMap.get(name) != null) {
                    System.err.println(name + " exists");
                    return null;
                }

                if (columnTypes == null) {
                    throw new IllegalArgumentException("columnTypes is null");
                }

                if (columnTypes.isEmpty()) {
                    throw new IllegalArgumentException(
                            "ColumnTypes list is empty");
                }
                Path newTablePath = tablesDirPath.resolve(name);
                newTablePath.toFile().mkdir();
                Path signature = newTablePath.resolve("signature.tsv");
                try {
                    if (!signature.toFile().createNewFile()) {
                        throw new IllegalArgumentException(
                                "signature making error");
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error creating table "
                            + name + ": " + e.getMessage());
                }

                FileOutputStream fileOutputStream;
                try {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (Class<?> type : columnTypes) {
                        if (type == null) {
                            throw new IllegalArgumentException(
                                    "wrong column type");
                        }
                        String typeString = Types.classToString(type);
                        if (typeString == null) {
                            throw new IllegalArgumentException(
                                    "wrong column type");
                        }
                        stringBuilder.append(type);
                        stringBuilder.append(' ');
                    }

                    String typeString = stringBuilder.toString();

                    typeString = typeString.substring(0,
                            typeString.length() - 1);
                    fileOutputStream = new FileOutputStream(signature.toFile());
                    fileOutputStream.getChannel().truncate(0);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                            fileOutputStream);
                    DataOutputStream dataOutputStream = new DataOutputStream(
                            bufferedOutputStream);
                    dataOutputStream.write(typeString.getBytes("UTF-8"));
                    dataOutputStream.close();

                } catch (IOException e) {
                    throw new IllegalArgumentException("Error creating table "
                            + name + ": " + e.getMessage());
                }

                Table newTable = new Tabledb(newTablePath, name, this,
                        columnTypes);
                tableMap.put(name, newTable);
                System.out.println("created");
                return newTable;
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        }
    }

    public Table createStoreableTable(final String[] arguments) {
        String[] types = Arrays.copyOfRange(arguments, 2, arguments.length);
        String name = arguments[1];

        if (types[0].charAt(0) != '('
                || types[types.length - 1].charAt(types[types.length - 1]
                        .length() - 1) != ')') {
            throw new IllegalArgumentException(
                    "You must specify types in brackets.");
        }
        types[0] = types[0].substring(1, types[0].length());
        types[types.length - 1] = types[types.length - 1].substring(0,
                types[types.length - 1].length() - 1);
        List<Class<?>> listOfClasses = new ArrayList<Class<?>>();
        for (String string : types) {
            if (Types.stringToClass(string) == null) {
                throw new IllegalArgumentException("Wrong type \"" + string
                        + "\".");
            }
            listOfClasses.add(Types.stringToClass(string));
        }
        return this.createTable(name, listOfClasses);
    }

    public void removeTable(final String name) {
        try {
            if (name != null && !name.equals("")) {
                tablesDirPath.resolve(name);
                Table removedTable = tableMap.remove(name);
                if (removedTable == null) {
                    throw new IllegalStateException(name + " does not exist");
                } else {
                    if (currentTable == removedTable) {
                        currentTable = null;
                    }
                    ((Tabledb) removedTable).deleteTable();
                    System.out.println("dropped");
                }
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Table getDataBase() {
        return currentTable;
    }

    public Table getTable(final String name) {
        try {
            if (name != null && !name.equals("")) {
                tablesDirPath.resolve(name);
                Table singleTable = tableMap.get(name);
                if (singleTable != null) {
                    checkTable(singleTable);
                }
                return singleTable;
            } else {
                throw new IllegalArgumentException("Null name.");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("table name " + name
                    + " is incorrect. " + e.getMessage());
        }
    }

    public void checkTable(final Table singleTable) {
        try {
            Path tempDirPath = tablesDirPath;
            Scanner scanner;
            scanner = new Scanner(tempDirPath.resolve(singleTable.getName())
                    .resolve("signature.tsv"));
            String typesString = scanner.nextLine();
            checkColumnTypes(typesString);
            scanner.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("table named "
                    + singleTable.getName() + " is incorrect: "
                    + e.getMessage());
        }

        List<String> keys = singleTable.list();
        for (String key : keys) {
            singleTable.get(key);
        }
    }

    public static void checkColumnTypes(final String oldTypesString)
            throws IllegalArgumentException {
        String typesString = "";
        String prefix = "class java.lang.";
        if (oldTypesString.startsWith(prefix)) {
            typesString = oldTypesString.substring(prefix.length());
        } else {
            throw new IllegalArgumentException("Wrong signature.tsv format.");
        }
        String[] types = typesString.split(" class java.lang.");
        for (String type : types) {
            if (!type.equals("String")) {
                type = Character.toLowerCase(type.charAt(0))
                        + type.substring(1);
            }
            if (type.equals("integer")) {
                type = "int";
            }
            if (Types.stringToClass(type) == null) {
                throw new IllegalArgumentException("Class " + type
                        + " is not supported.");
            }
        }
    }

    @Override
    public Storeable deserialize(final Table table, final String value)
            throws ParseException {
        return serializer.deserialize(table, value);
    }

    @Override
    public String serialize(final Table table, final Storeable value) {
        return serializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(final Table table) {
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        List<Object> values = new ArrayList<>(table.getColumnsCount());

        return new Storeabledb(values);
    }

    @Override
    public Storeable createFor(final Table table, final List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null || values == null || values.isEmpty()) {
            throw new IllegalArgumentException("null value or table");
        }

        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("invalid values count");
        }
        List<Object> objectValues = new ArrayList<>(values);

        return new Storeabledb(objectValues);
    }

    @Override
    public List<String> getTableNames() {
        return new ArrayList<String>(tableMap.keySet());
    }

}
