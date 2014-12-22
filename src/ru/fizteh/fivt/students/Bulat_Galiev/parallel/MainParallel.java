package ru.fizteh.fivt.students.Bulat_Galiev.parallel;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.InterpreterPackage.Command;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.InterpreterPackage.Interpreter;
import ru.fizteh.fivt.students.Bulat_Galiev.parallel.InterpreterPackage.StopInterpretationException;

public final class MainParallel {
    private MainParallel() {
        // Disable instantiation to this class.
    }

    public static void main(final String[] arg) {
        try {
            String databaseDir = System.getProperty("fizteh.db.dir");
            if (databaseDir == null) {
                System.err.println("specify the path to fizteh.db.dir");
                System.exit(-1);
            }
            TableProvider provider = new TabledbProviderFactory().create(databaseDir);
            run(provider, arg);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            System.exit(-1);
        }
    }

    private static void run(final TableProvider provider, final String[] arg) throws Exception {
        new Interpreter(provider, new Command[] {new Command("put", 2, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    ((TabledbProvider) provider).checkTable(curTable);
                    Storeable storeableValue;
                    try {
                        storeableValue = ((Tabledb) curTable).getLocalProvider().deserialize(curTable, arg[2]);
                    } catch (ParseException e) {
                        throw new StopInterpretationException("Deserialising problem: " + e.getMessage());
                    }
                    Storeable putValue = curTable.put(arg[1], storeableValue);
                    if (putValue == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println(provider.serialize(curTable, putValue));
                    }
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("get", 1, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    ((TabledbProvider) provider).checkTable(curTable);
                    Storeable getValue = ((Tabledb) curTable).get(arg[1]);
                    if (getValue == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("found");
                        System.out.println(provider.serialize(curTable, getValue));
                    }
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("remove", 1, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    ((TabledbProvider) provider).checkTable(curTable);
                    Storeable getValue = curTable.remove(arg[1]);
                    if (getValue != null) {
                        System.out.println("removed");
                    } else {
                        System.err.println("not found");
                    }
                } else {
                    System.err.println("no table selected");
                }
            }
        }), new Command("list", 0, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    System.out.println(String.join(", ", curTable.list()));
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("size", 0, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    System.out.println(curTable.size());
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("commit", 0, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    try {
                        System.out.println(curTable.commit());
                    } catch (IOException e) {
                        throw new StopInterpretationException("Error writing table " + curTable.getName());
                    }
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("rollback", 0, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    System.out.println(curTable.rollback());
                } else {
                    throw new StopInterpretationException("no table selected");
                }
            }
        }), new Command("create", 2, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {

                if (arg[2].charAt(0) != '(' || arg[2].charAt(arg[2].length() - 1) != ')') {
                    throw new IllegalArgumentException("You must specify types in brackets.");
                }
                String types = arg[2].substring(1, arg[2].length() - 1);
                StringTokenizer argtok = new StringTokenizer(types, " ", false);
                int i = 0;
                String[] args = new String[argtok.countTokens() + 1];
                args[i++] = arg[1];
                while (argtok.hasMoreTokens()) {
                    args[i++] = argtok.nextToken();
                }
                if (((TabledbProvider) provider).createStoreableTable(args) != null) {
                    System.out.println("created");
                } else {
                    throw new StopInterpretationException(arg[1] + " exists");
                }
            }
        }), new Command("use", 1, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                String name = arg[1];
                try {
                    Table curTable = ((TabledbProvider) provider).getDataBase();
                    if (curTable != null) {
                        int diff = ((Tabledb) curTable).getNumberOfUncommittedChanges();
                        if (diff != 0) {
                            System.out.println(diff + " unsaved changes");
                            return;
                        }
                    }
                    ((TabledbProvider) provider).changeCurTable(name);
                    System.out.println("using " + name);
                } catch (IllegalStateException e) {
                    throw new StopInterpretationException(name + " does not exist");
                }
            }
        }), new Command("drop", 1, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                String name = arg[1];
                try {
                    provider.removeTable(name);
                    System.out.println("dropped");
                } catch (IllegalStateException e) {
                    throw new StopInterpretationException(name + " does not exist");
                } catch (IOException e) {
                    throw new StopInterpretationException(e.getMessage());
                }
            }
        }), new Command("show", 1, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                if (arg[1].equals("tables")) {
                    Set<String> keys = ((TabledbProvider) provider).getKeySet();
                    for (String current : keys) {
                        System.out.println(current + " " + ((TabledbProvider) provider).getTable(current).size());
                    }
                } else {
                    throw new StopInterpretationException("show " + arg[1] + " is incorrect command");
                }
            }
        }), new Command("exit", 0, new BiConsumer<TableProvider, String[]>() {
            @Override
            public void accept(final TableProvider state, final String[] arg) {
                Table curTable = ((TabledbProvider) provider).getDataBase();
                if (curTable != null) {
                    int diff = ((Tabledb) ((TabledbProvider) provider).getDataBase()).getNumberOfUncommittedChanges();
                    if (diff == 0) {
                        System.exit(0);
                    } else {
                        System.err.println(diff + " unsaved changes. Do commit or rollback command before exit");
                    }
                } else {
                    System.exit(0);
                }
            }
        }) }).run(arg);
    }
}
