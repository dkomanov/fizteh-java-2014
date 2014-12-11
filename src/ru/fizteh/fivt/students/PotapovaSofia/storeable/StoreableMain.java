package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.Command;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.Interpreter;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter.StopInterpretationException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StoreableMain {
    public static final String SPLIT_BY_SPACES_NOT_IN_BRACKETS_REGEX = "\\s*(\".*\"|\\(.*\\)|\\[.*\\]|[^\\s]+)\\s*";
    public static final String IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String ILLEGAL_TABLE_NAME_PATTERN = ".*\\.|\\..*|.*(/|\\\\).*";
    public static final String CODING = "UTF-8";
    public static final String TABLE_SIGNATURE = "signature.tsv";

    public static final Map<String, Class> availableTypes = new HashMap<>();
    public static final Map<Class, String> avaliableClasses = new HashMap<>();
    public static final Map<Class<?>, Function<String, Object>> parseTypes;

    static {
        String[] primitiveNames = new String[]{"int", "long", "byte", "float", "double", "boolean", "String"};
        Class[] classes = new Class[]{Integer.class, Long.class, Byte.class, Float.class, Double.class,
                Boolean.class, String.class};

        for (int i = 0; i < primitiveNames.length; i++) {
            availableTypes.put(primitiveNames[i], classes[i]);
            avaliableClasses.put(classes[i], primitiveNames[i]);
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
        parseTypes = Collections.unmodifiableMap(unitializerMap);
    }

    public static void main(String[] args) throws Exception {
        String pathName = System.getProperty("fizteh.db.dir");
        if (pathName == null) {
            System.err.println("You must specify db.file via -Ddb.file JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new DbTableProviderFactory();
        TableState state = new TableState(factory.create(pathName));

        Interpreter dbInterpreter = new Interpreter(state, new Command[]{
                new Command("put", 2, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            try {
                                Storeable oldValue = currentTable.put(args[0],
                                        tableProvider.deserialize(currentTable, args[1]));
                                if (oldValue != null) {
                                    System.out.println("overwrite");
                                    System.out.println(oldValue);
                                } else {
                                    System.out.println("new");
                                }
                            } catch (ColumnFormatException | ParseException e) {
                                System.out.println("wrong type: (" + e.getMessage() + ")");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            Storeable value = currentTable.get(args[0]);
                            if (value != null) {
                                System.out.println("found");
                                System.out.println(tableProvider.serialize(currentTable, value));
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            Storeable removedValue = currentTable.remove(args[0]);
                            if (removedValue != null) {
                                System.out.println("removed");
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(String.join(", ", currentTable.list()));
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(currentTable.size());
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
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
                    }
                }),
                new Command("rollback", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null) {
                            System.out.println(currentTable.rollback());
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("create", 2, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        String[] types = args[1].substring(1, args[1].length() - 1).split(" ");
                        List<Class<?>> typesList = new ArrayList<>();
                        boolean isCreating = true;
                        for (String type : types) {
                            Class<?> typeClass = availableTypes.get(type);
                            if (typeClass != null) {
                                typesList.add(typeClass);
                            } else {
                                System.out.println("wrong type (" + type + " is not avaliable type)");
                                isCreating = false;
                            }
                        }
                        if (isCreating) {
                            TableProvider tableProvider = state.getTableProvider();
                            try {
                                if (tableProvider.createTable(args[0], typesList) != null) {
                                    System.out.println("created");
                                } else {
                                    System.out.println(args[0] + " exists");
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table newTable = tableProvider.getTable(args[0]);
                        DbTable usedTable = (DbTable) state.getUsedTable();
                        if (newTable != null) {
                            if (usedTable != null && (usedTable.getNumberOfUncommittedChanges() > 0)
                                    && (usedTable != newTable)) {
                                System.out.println(usedTable.getNumberOfUncommittedChanges() + " unsaved changes");
                            } else {
                                state.setUsedTable(newTable);
                                System.out.println("using " + args[0]);
                            }
                        } else {
                            System.out.println(args[0] + " not exists");
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        TableProvider tableProvider = state.getTableProvider();
                        Table currentTable = state.getUsedTable();
                        if (currentTable != null && currentTable.getName().equals(args[0])) {
                            state.setUsedTable(null);
                        }
                        try {
                            tableProvider.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (IllegalStateException e) {
                            System.out.println(args[0] + " not exists");
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                        if (args[0].equals("tables")) {
                            TableProvider tableProvider = state.getTableProvider();
                            List<String> tableNames = tableProvider.getTableNames();
                            System.out.println("table_name row_count");
                            for (String name : tableNames) {
                                Table curTable = tableProvider.getTable(name);
                                System.out.println(curTable.getName() + " " + curTable.size());
                            }
                         } else {
                            System.out.println("Wrong command: " + args[0]);
                        }
                    }
                }),
                new Command("exit", 0, new BiConsumer<TableState, String[]>() {
                    @Override
                    public void accept(TableState state, String[] args) {
                    }
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
