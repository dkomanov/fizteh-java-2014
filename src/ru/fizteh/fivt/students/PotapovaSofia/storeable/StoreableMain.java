package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.Command;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.Interpreter;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.StopInterpretationException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;

public class StoreableMain {
    public static final String SPLIT_BY_SPACES_NOT_IN_BRACKETS_REGEX = "\\s*(\".*\"|\\(.*\\)|\\[.*\\]|[^\\s]+)\\s*";
    public static final String IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";
    public static final String ENCODING = "UTF-8";
    public static final String TABLE_SIGNATURE = "signature.tsv";

    public static final Map<String, Class> AVAILABLE_TYPES = new HashMap<>();
    public static final Map<Class, String> AVAILABLE_CLASSES = new HashMap<>();
    public static final Map<Class<?>, Function<String, Object>> PARSE_TYPES;

    static {
        String[] primitiveNames = new String[]{"int", "long", "byte", "float", "double", "boolean", "String"};
        Class[] classes = new Class[]{Integer.class, Long.class, Byte.class, Float.class, Double.class,
                Boolean.class, String.class};

        for (int i = 0; i < primitiveNames.length; i++) {
            AVAILABLE_TYPES.put(primitiveNames[i], classes[i]);
            AVAILABLE_CLASSES.put(classes[i], primitiveNames[i]);
        }
    }

    static {
        Map<Class<?>, Function<String, Object>> unitializerMap = new HashMap<>();
        unitializerMap.put(Integer.class, Integer::parseInt);
        unitializerMap.put(Long.class, Long::parseLong);
        unitializerMap.put(Byte.class, Byte::parseByte);
        unitializerMap.put(Float.class, Float::parseFloat);
        unitializerMap.put(Double.class, Double::parseDouble);
        unitializerMap.put(Boolean.class, string -> {
            if (!string.matches("(?i)true|false")) {
                throw new ColumnFormatException("Expected 'true' or 'false'");
            }
            return Boolean.parseBoolean(string);
        });
        unitializerMap.put(String.class, string -> {
            if (string.charAt(0) != '"' || string.charAt(string.length() - 1) != '"') {
                throw new ColumnFormatException("String must be quoted");
            }
            return string.substring(1, string.length() - 1);
        });
        PARSE_TYPES = Collections.unmodifiableMap(unitializerMap);
    }

    public static void main(String[] args) throws IOException {
        String pathName = System.getProperty("fizteh.db.dir");
        if (pathName == null) {
            System.err.println("You must specify db.file via -Ddb.file JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new DbTableProviderFactory();
        TableState state = new TableState(factory.create(pathName));

        start(state, args);
    }

    private static void start(TableState state, String[] args) {
        Interpreter dbInterpreter = new Interpreter(state, new Command[]{
                new Command("put", 2, (state1, args1) -> {
                    TableProvider tableProvider = state1.getTableProvider();
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        try {
                            Storeable oldValue = currentTable.put(args1[0],
                                    tableProvider.deserialize(currentTable, args1[1]));
                            if (oldValue != null) {
                                System.out.println("overwrite");
                                System.out.println(tableProvider.serialize(currentTable, oldValue));
                            } else {
                                System.out.println("new");
                            }
                        } catch (ColumnFormatException | ParseException e) {
                            System.out.println("wrong type: (" + e.getMessage() + ")");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("get", 1, (state1, args1) -> {
                    TableProvider tableProvider = state1.getTableProvider();
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        Storeable value = currentTable.get(args1[0]);
                        if (value != null) {
                            System.out.println("found");
                            System.out.println(tableProvider.serialize(currentTable, value));
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("remove", 1, (state1, args1) -> {
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        Storeable removedValue = currentTable.remove(args1[0]);
                        if (removedValue != null) {
                            System.out.println("removed");
                        } else {
                            System.out.println("not found");
                        }
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("list", 0, (state1, args1) -> {
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        System.out.println(String.join(", ", currentTable.list()));
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("size", 0, (state1, args1) -> {
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        System.out.println(currentTable.size());
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("commit", 0, (state1, args1) -> {
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        try {
                            System.out.println(currentTable.commit());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("rollback", 0, (state1, args1) -> {
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null) {
                        System.out.println(currentTable.rollback());
                    } else {
                        System.out.println("no table");
                    }
                }),
                new Command("create", 2, (state1, args1) -> {
                    String[] types = args1[1].substring(1, args1[1].length() - 1).split(" ");
                    List<Class<?>> typesList = new ArrayList<>();
                    boolean isCreating = true;
                    for (String type : types) {
                        Class<?> typeClass = AVAILABLE_TYPES.get(type);
                        if (typeClass != null) {
                            typesList.add(typeClass);
                        } else {
                            System.out.println("wrong type (" + type + " is not avaliable type)");
                            isCreating = false;
                        }
                    }
                    if (isCreating) {
                        TableProvider tableProvider = state1.getTableProvider();
                        try {
                            if (tableProvider.createTable(args1[0], typesList) != null) {
                                System.out.println("created");
                            } else {
                                System.out.println(args1[0] + " exists");
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }),
                new Command("use", 1, (state1, args1) -> {
                    TableProvider tableProvider = state1.getTableProvider();
                    Table newTable = tableProvider.getTable(args1[0]);
                    DbTable usedTable = (DbTable) state1.getUsedTable();
                    if (newTable != null) {
                        if (usedTable != null && (usedTable.getNumberOfUncommittedChanges() > 0)
                                && (usedTable != newTable)) {
                            System.out.println(usedTable.getNumberOfUncommittedChanges() + " unsaved changes");
                        } else {
                            state1.setUsedTable(newTable);
                            System.out.println("using " + args1[0]);
                        }
                    } else {
                        System.out.println(args1[0] + " not exists");
                    }
                }),
                new Command("drop", 1, (state1, args1) -> {
                    TableProvider tableProvider = state1.getTableProvider();
                    Table currentTable = state1.getUsedTable();
                    if (currentTable != null && currentTable.getName().equals(args1[0])) {
                        state1.setUsedTable(null);
                    }
                    try {
                        tableProvider.removeTable(args1[0]);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(args1[0] + " not exists");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }),
                new Command("show", 1, (state1, args1) -> {
                    if (args1[0].equals("tables")) {
                        TableProvider tableProvider = state1.getTableProvider();
                        List<String> tableNames = tableProvider.getTableNames();
                        System.out.println("table_name row_count");
                        for (String name : tableNames) {
                            Table curTable = tableProvider.getTable(name);
                            System.out.println(curTable.getName() + " " + curTable.size());
                        }
                    } else {
                        System.out.println("Wrong command: " + args1[0]);
                    }
                }),
                new Command("exit", 0, (state1, args1) -> {
                })
        });
        dbInterpreter.run(args);
    }
    public static void exit(TableState state) throws StopInterpretationException {
        if (state != null) {
            TableProvider tableProvider = state.getTableProvider();
            DbTable usedTable = (DbTable) state.getUsedTable();
            if (usedTable != null && (usedTable.getNumberOfUncommittedChanges() > 0)) {
                System.out.println(usedTable.getNumberOfUncommittedChanges() + " unsaved changes");
            } else {
                throw new StopInterpretationException();
            }
        } else {
            throw new StopInterpretationException();
        }
    }
}
