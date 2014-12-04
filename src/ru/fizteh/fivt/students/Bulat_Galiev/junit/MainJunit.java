package ru.fizteh.fivt.students.Bulat_Galiev.junit;

import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.junit.InterpreterPackage.*;

import java.util.function.BiConsumer;

public final class MainJunit {
    private MainJunit() {
        // Disable instantiation to this class.
    }

    public static void main(final String[] arg) {
        try {
            String databaseDir = System.getProperty("fizteh.db.dir");
            if (databaseDir == null) {
                System.err.println("specify the path to fizteh.db.dir");
                System.exit(-1);
            }
            TableProvider provider = new TabledbProviderFactory()
                    .create(databaseDir);
            run(provider, arg);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            System.exit(-1);
        }
    }

    private static void run(final TableProvider provider, final String[] arg)
            throws Exception {
        new Interpreter(provider, new Command[] {
                new Command("put", 2,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    String putValue = curTable.put(arg[1],
                                            arg[2]);
                                    if (putValue == null) {
                                        System.out.println("new");
                                    } else {
                                        System.out.println("overwrite");
                                        System.out.println(putValue);
                                    }
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("get", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    String getValue = curTable.get(arg[1]);
                                    if (getValue == null) {
                                        System.out.println("not found");
                                    } else {
                                        System.out.println("found");
                                        System.out.println(getValue);
                                    }
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("remove", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    String getValue = curTable.remove(arg[1]);
                                    if (getValue != null) {
                                        System.out.println("removed");
                                    } else {
                                        System.err.println("not found");
                                    }
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("list", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    List<String> list = curTable.list();
                                    int iteration = 0;
                                    for (String current : list) {
                                        iteration++;
                                        System.out.print(current);
                                        if (iteration != list.size()) {
                                            System.out.print(", ");
                                        }
                                    }
                                    System.out.println();
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("size", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    System.out.println(curTable.size());
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("commit", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    System.out.println(curTable.commit());
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("rollback", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    System.out.println(curTable.rollback());
                                } else {
                                    throw new StopInterpretationException(
                                            "no table selected");
                                }
                            }
                        }),
                new Command("create", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                if (provider.createTable(arg[1]) != null) {
                                    System.out.println("created");
                                } else {
                                    throw new StopInterpretationException(
                                            arg[1] + " exists");
                                }
                            }
                        }),
                new Command("use", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                String name = arg[1];
                                try {
                                    Table curTable = ((TabledbProvider) provider)
                                            .getDataBase();
                                    if (curTable != null) {
                                        int diff = ((Tabledb) curTable)
                                                .getChangedRecordsNumber();
                                        if (diff != 0) {
                                            System.out.println(diff
                                                    + " unsaved changes");
                                            return;
                                        }
                                    }
                                    TabledbProvider.changeCurTable(name);
                                    System.out.println("using " + name);
                                } catch (IllegalStateException e) {
                                    throw new StopInterpretationException(name
                                            + " does not exist");
                                }
                            }
                        }),
                new Command("drop", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                String name = arg[1];
                                try {
                                    provider.removeTable(name);
                                    System.out.println("dropped");
                                } catch (IllegalStateException e) {
                                    throw new StopInterpretationException(name
                                            + " does not exist");
                                }
                            }
                        }),
                new Command("show", 1,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                if (arg[1].equals("tables")) {
                                    Set<String> keys = ((TabledbProvider) provider)
                                            .getKeySet();
                                    for (String current : keys) {
                                        System.out.println(current
                                                + " "
                                                + ((TabledbProvider) provider)
                                                        .getTable(current)
                                                        .size());
                                    }
                                } else {
                                    throw new StopInterpretationException(
                                            "show " + arg[1]
                                                    + " is incorrect command");
                                }
                            }
                        }),
                new Command("exit", 0,
                        new BiConsumer<TableProvider, String[]>() {
                            @Override
                            public void accept(final TableProvider state, final String[] arg) {
                                Table curTable = ((TabledbProvider) provider)
                                        .getDataBase();
                                if (curTable != null) {
                                    int diff = ((Tabledb) ((TabledbProvider) provider)
                                            .getDataBase())
                                            .getChangedRecordsNumber();
                                    if (diff == 0) {
                                        System.exit(0);
                                    } else {
                                        System.err
                                                .println(diff
                                                        + " unsaved changes. "
                                                        + "Do commit or rollback command before exit");
                                    }
                                } else {
                                    System.exit(0);
                                }
                            }
                        }) }).run(arg);
    }
}
