package ru.fizteh.fivt.students.ilivanov.JUnit;

import ru.fizteh.fivt.students.ilivanov.JUnit.TableInterfaces.Table;

import java.util.ArrayList;

public class ShellBigBoss {
    final private FileMapProvider database;
    private MultiFileMap current;

    public ShellBigBoss(final String location) {
        FileMapProviderFactory factory = new FileMapProviderFactory();
        database = factory.create(location);
        current = null;
    }

    void printException(final Throwable e) {
        if (e.getCause() != null) {
            printException(e.getCause());
        }
        for (Throwable suppressed : e.getSuppressed()) {
            printException(suppressed);
        }
        System.err.println(e.getMessage());
    }

    final private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("create", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("create: too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("create: too few arguments");
                        return -1;
                    }
                    try {
                        Table table = database.createTable(args.get(0));
                        if (table == null) {
                            System.out.printf("%s exists\n", args.get(0));
                            return 0;
                        }
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    System.out.println("created");
                    return 0;
                }
            }),
            new Shell.ShellCommand("drop", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("drop: too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("drop: too few arguments");
                        return -1;
                    }
                    try {
                        database.removeTable(args.get(0));
                    } catch (IllegalStateException e) {
                        System.out.printf("%s doesn't exists\n", args.get(0));
                        return 0;
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    if (current != null && current.getName().equals(args.get(0))) {
                        current = null;
                    }
                    System.out.println("dropped");
                    return 0;
                }
            }),
            new Shell.ShellCommand("use", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("use: too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("use: too few arguments");
                        return -1;
                    }
                    if (current != null && current.uncommittedChanges() != 0) {
                        System.out.printf("%d unsaved changes\n", current.uncommittedChanges());
                        return 0;
                    }
                    try {
                        MultiFileMap newTable = database.getTable(args.get(0));
                        if (newTable != null) {
                            current = newTable;
                        } else {
                            System.out.printf("%s doesn't exists\n", args.get(0));
                            return 0;
                        }
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    System.out.printf("using %s\n", args.get(0));
                    return 0;
                }
            }),
            new Shell.ShellCommand("show", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() < 1 || (args.size() == 1 && !args.get(0).equals("tables"))) {
                        System.err.printf("unknown command: show\n");
                        return -1;
                    }
                    if (args.size() > 1) {
                        System.err.println("show tables: too many arguments");
                        return -1;
                    }
                    database.showTables();
                    return 0;
                }
            }),
            new Shell.ShellCommand("put", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 2) {
                        System.err.println("put: too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("put: too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("put: no table");
                        return 0;
                    }
                    String value = current.put(args.get(0), args.get(1));
                    if (value == null) {
                        System.out.println("new");
                    } else {
                        System.out.printf("overwrite\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("get", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("get: too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("get: too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("get: no table");
                        return 0;
                    }
                    String value = current.get(args.get(0));
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.printf("found\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("remove", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("remove: too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("remove: too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("no table");
                        return 0;
                    }
                    if (current.remove(args.get(0)) != null) {
                        System.out.println("removed");
                    } else {
                        System.out.println("not found");
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("list", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("list: too many arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("no table");
                        return 0;
                    }
                    current.list().forEach(System.out::println);
                    return 0;
                }
            }),
            new Shell.ShellCommand("size", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("size: too many arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.err.println("size: no table");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.size());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("commit", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("commit: too many arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.err.println("commit: no table");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.commit());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("rollback", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("rollback: too many arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.err.println("commit: no table");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.rollback());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("exit", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    shell.stop();
                    if (current != null) {
                        current.commit();
                    }
                    return 0;
                }
            })
    };

    public void integrate(final Shell shell) {
        for (Shell.ShellCommand command : commands) {
            shell.addCommand(command);
        }
    }
}
