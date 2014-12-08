package ru.fizteh.fivt.students.ilivanov.Storeable;

import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.Table;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;

public class ShellBigBoss {
    private FileMapProvider database;
    private MultiFileMap current;

    public ShellBigBoss(FileMapProvider database) {
        this.database = database;
        current = null;
    }

    void printException(Throwable e, PrintStream errorLog) {
        if (e.getCause() != null) {
            printException(e.getCause(), errorLog);
        }
        for (Throwable suppressed : e.getSuppressed()) {
            printException(suppressed, errorLog);
        }
        if (e.getMessage() != null && !e.getMessage().equals("")) {
            errorLog.println(e.getMessage());
        }
    }

    ArrayList<String> parseArguments(int argCount, String argString) {
        ArrayList<String> args = new ArrayList<>();
        int argsRead = 0;
        String last = "";
        int start = 0;
        for (int i = 0; i < argString.length(); i++) {
            if (Character.isWhitespace(argString.charAt(i))) {
                if (start != i) {
                    args.add(argString.substring(start, i));
                    argsRead++;
                }
                start = i + 1;
                if (argsRead == argCount - 1) {
                    last = argString.substring(start, argString.length());
                    break;
                }
            }
        }
        last = last.trim();
        if (!last.equals("")) {
            args.add(last);
        }
        return args;
    }

    private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("create", false, new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    args = parseArguments(2, args.get(0));
                    if (args.size() > 2) {
                        shell.writer.println("create: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        shell.writer.println("wrong type (type not specified)");
                        return -1;
                    }
                    try {
                        if (args.get(1).length() < 2) {
                            shell.writer.println("wrong type (wrong argument format)");
                            return -1;
                        }
                        if (args.get(1).charAt(0) != '(') {
                            shell.writer.println("wrong type (wrong argument format)");
                            return -1;
                        }
                        if (args.get(1).charAt(args.get(1).length() - 1) != ')') {
                            shell.writer.println("wrong type (wrong argument format)");
                            return -1;
                        }
                        String[] typeNames = args.get(1).substring(1, args.get(1).length() - 1).trim().split("\\s+");
                        ArrayList<Class<?>> columnTypes = new ArrayList<>();
                        for (String typeName : typeNames) {
                            if (typeName.equals("int")) {
                                columnTypes.add(Integer.class);
                            } else if (typeName.equals("long")) {
                                columnTypes.add(Long.class);
                            } else if (typeName.equals("byte")) {
                                columnTypes.add(Byte.class);
                            } else if (typeName.equals("float")) {
                                columnTypes.add(Float.class);
                            } else if (typeName.equals("double")) {
                                columnTypes.add(Double.class);
                            } else if (typeName.equals("boolean")) {
                                columnTypes.add(Boolean.class);
                            } else if (typeName.equals("String")) {
                                columnTypes.add(String.class);
                            } else {
                                shell.writer.println(String.format("wrong type (%s is not supported)",
                                        typeName));
                                return -1;
                            }
                        }
                        Table table = database.createTable(args.get(0), columnTypes);
                        if (table == null) {
                            shell.writer.printf("%s exists%s", args.get(0), System.lineSeparator());
                            return 0;
                        }
                    } catch (ColumnFormatException e) {
                        shell.writer.println(String.format("wrong type (%s)", e.getMessage()));
                        return -1;
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    } catch (IOException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    shell.writer.println("created");
                    return 0;
                }
            }),
            new Shell.ShellCommand("drop", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("drop: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        shell.writer.println("drop: Too few arguments");
                        return -1;
                    }
                    boolean inUse = args.get(0).equals(current.getName());
                    try {
                        database.removeTable(args.get(0));
                    } catch (IllegalStateException e) {
                        shell.writer.printf("%s not exists%s", args.get(0), System.lineSeparator());
                        return 0;
                    } catch (RuntimeException | IOException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    if (current != null && inUse) {
                        current = null;
                    }
                    shell.writer.println("dropped");
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
            new Shell.ShellCommand("use", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("use: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        shell.writer.println("use: Too few arguments");
                        return -1;
                    }
                    if (current != null && current.uncommittedChanges() != 0) {
                        shell.writer.printf("%d unsaved changes%s",
                                current.uncommittedChanges(), System.lineSeparator());
                        return 0;
                    }
                    try {
                        MultiFileMap newTable = database.getTable(args.get(0));
                        if (newTable != null) {
                            current = newTable;
                        } else {
                            shell.writer.printf("%s not exists%s", args.get(0), System.lineSeparator());
                            return 0;
                        }
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    shell.writer.printf("using %s%s", args.get(0), System.lineSeparator());
                    return 0;
                }
            }),
            new Shell.ShellCommand("commit", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("commit: Too many arguments");
                        return -1;
                    }
                    try {
                        shell.writer.println(current.commit());
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    } catch (IOException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("rollback", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("rollback: Too many arguments");
                        return -1;
                    }
                    try {
                        shell.writer.println(current.rollback());
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("size", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("rollback: Too many arguments");
                        return -1;
                    }
                    try {
                        shell.writer.println(current.size());
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("put", false, new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    args = parseArguments(2, args.get(0));
                    if (args.size() > 2) {
                        shell.writer.println("put: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        shell.writer.println("put: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        shell.writer.println("no table");
                        return 0;
                    }
                    try {
                        Storeable value = current.put(args.get(0), database.deserialize(current, args.get(1)));
                        if (value == null) {
                            shell.writer.println("new");
                        } else {
                            shell.writer.printf("overwrite%s%s%s",
                                    System.lineSeparator(), database.serialize(current, value), System.lineSeparator());
                        }
                    } catch (ColumnFormatException | ParseException e) {
                        shell.writer.printf("wrong type (%s)%s", e.getMessage(), System.lineSeparator());
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("get", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("get: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        shell.writer.println("get: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        shell.writer.println("no table");
                        return 0;
                    }
                    Storeable value = current.get(args.get(0));
                    if (value == null) {
                        shell.writer.println("not found");
                    } else {
                        shell.writer.printf("found%s%s%s",
                                System.lineSeparator(), database.serialize(current, value), System.lineSeparator());
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("remove", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("remove: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        shell.writer.println("remove: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        shell.writer.println("no table");
                        return 0;
                    }
                    Storeable value = current.remove(args.get(0));
                    if (value != null) {
                        shell.writer.printf("removed%s", System.lineSeparator());
                    } else {
                        shell.writer.println("not found");
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

            new Shell.ShellCommand("exit", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    shell.stop();
                    if (current != null) {
                        try {
                            current.commit();
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                    return 0;
                }
            })
    };

    public void integrate(Shell shell) {
        for (Shell.ShellCommand command : commands) {
            shell.addCommand(command);
        }
        shell.addExitFunction(new Shell.ShellCommand(null, (shellArg, args) -> {
            try {
                if (current != null) {
                    current.rollback();
                }
            } catch (Exception e) {
                printException(e, shellArg.writer);
            }
            return 0;
        }));
    }
}
