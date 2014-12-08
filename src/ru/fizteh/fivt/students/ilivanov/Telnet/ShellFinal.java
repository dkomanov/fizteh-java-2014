package ru.fizteh.fivt.students.ilivanov.Telnet;


import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Table;

import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;

public class ShellFinal {
    private FileMapProvider provider;
    private MultiFileMap current;
    private DatabaseServer server;
    private RemoteFileMapProviderFactory remoteFactory;
    private RemoteFileMapProvider remoteProvider;
    private RemoteFileUsing remoteCurrent;
    private boolean local;

    public ShellFinal(FileMapProvider provider) throws IOException {
        this.provider = provider;
        server = new DatabaseServer(provider);
        current = null;
        remoteFactory = new RemoteFileMapProviderFactory();
        remoteProvider = null;
        remoteCurrent = null;
        local = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
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
                        Table table;
                        if (local) {
                            table = provider.createTable(args.get(0), columnTypes);
                        } else {
                            table = remoteProvider.createTable(args.get(0), columnTypes);
                        }
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
                        if (local) {
                            provider.removeTable(args.get(0));
                        } else {
                            remoteProvider.removeTable(args.get(0));
                        }
                    } catch (IllegalStateException e) {
                        shell.writer.printf("%s not exists%s", args.get(0), System.lineSeparator());
                        return 0;
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    } catch (IOException e) {
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
                    try {
                        if (local) {
                            if (current != null && current.uncommittedChanges() != 0) {
                                shell.writer.printf("%d unsaved changes%s",
                                        current.uncommittedChanges(), System.lineSeparator());
                                return 0;
                            }
                            MultiFileMap newTable = provider.getTable(args.get(0));
                            if (newTable != null) {
                                current = newTable;
                            } else {
                                shell.writer.printf("%s not exists%s", args.get(0), System.lineSeparator());
                                return 0;
                            }
                        } else {
                            RemoteFileUsing newTable = remoteProvider.getTable(args.get(0));
                            if (newTable != null) {
                                try {
                                    remoteProvider.activate(newTable);
                                } catch (RemoteFileMapProvider.UnsavedChangesException e) {
                                    shell.writer.println(e.getMessage());
                                    return 0;
                                }
                                remoteCurrent = newTable;
                            } else {
                                shell.writer.printf("%s not exists%s", args.get(0), System.lineSeparator());
                                return 0;
                            }
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
                        if (local) {
                            if (current == null) {
                                shell.writer.println("no table");
                                return 0;
                            }
                            shell.writer.println(current.commit());
                        } else {
                            if (remoteCurrent == null) {
                                shell.writer.println("no table");
                                return 0;
                            }
                            shell.writer.println(remoteCurrent.commit());
                        }
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
                        if (local) {
                            shell.writer.println(current.rollback());
                        } else {
                            shell.writer.println(remoteCurrent.rollback());
                        }
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
                        shell.writer.println("size: Too many arguments");
                        return -1;
                    }
                    try {
                        if (local) {
                            shell.writer.println(current.size());
                        } else {
                            shell.writer.println(remoteCurrent.size());
                        }
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
                    try {
                        Storeable value = null;
                        if (local) {
                            if (current == null) {
                                shell.writer.println("no table");
                                return 0;
                            }
                            value = current.put(args.get(0), provider.deserialize(current, args.get(1)));
                        } else {
                            if (remoteCurrent == null) {
                                shell.writer.println("no table");
                                return 0;
                            }
                            value = remoteCurrent.put(
                                    args.get(0), remoteProvider.deserialize(remoteCurrent, args.get(1)));
                        }
                        if (value == null) {
                            shell.writer.println("new");
                        } else {
                            String answer;
                            if (local) {
                                answer = provider.serialize(current, value);
                            } else {
                                answer = remoteProvider.serialize(remoteCurrent, value);
                            }
                            shell.writer.printf("overwrite%s%s%s",
                                    System.lineSeparator(), answer, System.lineSeparator());
                        }
                    } catch (ColumnFormatException | ParseException e) {
                        shell.writer.printf("wrong type (%s)%s", e.getMessage(), System.lineSeparator());
                    } catch (Exception e) {
                        printException(e, shell.writer);
                        return -1;
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
                    Storeable value;
                    if (local) {
                        if (current == null) {
                            shell.writer.println("no table");
                            return 0;
                        }
                        value = current.get(args.get(0));
                    } else {
                        if (remoteCurrent == null) {
                            shell.writer.println("no table");
                            return 0;
                        }
                        value = remoteCurrent.get(args.get(0));
                    }
                    if (value == null) {
                        shell.writer.println("not found");
                    } else {
                        String answer;
                        if (local) {
                            answer = provider.serialize(current, value);
                        } else {
                            answer = remoteProvider.serialize(remoteCurrent, value);
                        }
                        shell.writer.printf("found%s%s%s",
                                System.lineSeparator(), answer, System.lineSeparator());
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
                    Storeable value;
                    if (local) {
                        if (current == null) {
                            shell.writer.println("no table");
                            return 0;
                        }
                        value = current.remove(args.get(0));
                    } else {
                        if (remoteCurrent == null) {
                            shell.writer.println("no table");
                            return 0;
                        }
                        value = remoteCurrent.remove(args.get(0));
                    }
                    if (value != null) {
                        shell.writer.printf("removed%s", System.lineSeparator());
                    } else {
                        shell.writer.println("not found");
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("describe", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("describe: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        shell.writer.println("describe: Too few arguments");
                        return -1;
                    }
                    try {
                        Table newTable;
                        if (local) {
                            newTable = provider.getTable(args.get(0));
                        } else {
                            newTable = remoteProvider.getTable(args.get(0));
                        }
                        if (newTable != null) {
                            int columnCount = newTable.getColumnsCount();
                            for (int i = 0; i < columnCount; i++) {
                                if (newTable.getColumnType(i) == Integer.class) {
                                    shell.writer.print("int");
                                } else if (newTable.getColumnType(i) == Long.class) {
                                    shell.writer.print("long");
                                } else if (newTable.getColumnType(i) == Byte.class) {
                                    shell.writer.print("byte");
                                } else if (newTable.getColumnType(i) == Float.class) {
                                    shell.writer.print("float");
                                } else if (newTable.getColumnType(i) == Double.class) {
                                    shell.writer.print("double");
                                } else if (newTable.getColumnType(i) == Boolean.class) {
                                    shell.writer.print("boolean");
                                } else if (newTable.getColumnType(i) == String.class) {
                                    shell.writer.print("String");
                                }
                                if (i != columnCount - 1) {
                                    shell.writer.print(" ");
                                }
                            }
                            shell.writer.println();
                        } else {
                            shell.writer.println(String.format("%s not exists", args.get(0)));
                            return -1;
                        }
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("connect", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 2) {
                        shell.writer.println("connect: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        shell.writer.println("connect: Too few arguments");
                        return -1;
                    }
                    if (!local) {
                        shell.writer.println("not connected: already connected to a server");
                        return -1;
                    }
                    try {
                        remoteProvider = remoteFactory.connect(args.get(0), Integer.parseInt(args.get(1)));
                    } catch (UnknownHostException e) {
                        shell.writer.print("not connected: ");
                        shell.writer.println(String.format("Unknown hostname: %s", args.get(0)));
                        return -1;
                    } catch (IOException e) {
                        shell.writer.print("not connected: ");
                        printException(e, shell.writer);
                        return -1;
                    } catch (NumberFormatException e) {
                        shell.writer.print("not connected: ");
                        shell.writer.println("Illegal port format");
                        return -1;
                    } catch (IllegalArgumentException e) {
                        shell.writer.print("not connected: ");
                        shell.writer.println("Port out of range");
                        return -1;
                    } catch (RuntimeException e) {
                        shell.writer.print("not connected: ");
                        printException(e, shell.writer);
                        return -1;
                    }
                    local = false;
                    shell.writer.println("connected");
                    return 0;
                }
            }),
            new Shell.ShellCommand("disconnect", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("disconnect: Too many arguments");
                        return -1;
                    }
                    if (local) {
                        shell.writer.println("not connected");
                        return 0;
                    }
                    try {
                        remoteProvider.close();
                        remoteProvider = null;
                        remoteCurrent = null;
                    } catch (IOException e) {
                        printException(e, shell.writer);
                        return -1;
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    local = true;
                    shell.writer.println("disconnected");
                    return 0;
                }
            }),
            new Shell.ShellCommand("whereami", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("whereami: Too many arguments");
                        return -1;
                    }
                    if (local) {
                        shell.writer.println("local");
                        return 0;
                    }
                    try {
                        shell.writer.println(String.format("remote %s %d",
                                remoteProvider.getHost(), remoteProvider.getPort()));
                    } catch (RuntimeException e) {
                        printException(e, shell.writer);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("start", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("start: Too many arguments");
                        return -1;
                    }
                    if (server.isStarted()) {
                        shell.writer.println("not started: already started");
                    }
                    int newPort = 10001;
                    if (args.size() == 1) {
                        try {
                            newPort = Integer.parseInt(args.get(0));
                        } catch (NumberFormatException e) {
                            shell.writer.println("not started: Can't parse argument as integer");
                            return -1;
                        }
                    }
                    try {
                        server.start(newPort);
                    } catch (InterruptedException e) {
                        shell.writer.println("Critical error: main thread interrupted");
                        return -1;
                    } catch (DatabaseServer.ServerStartException e) {
                        shell.writer.println(String.format("not started: %s", e.getMessage()));
                        return -1;
                    }
                    shell.writer.println(String.format("started at %d", newPort));
                    return 0;
                }
            }),
            new Shell.ShellCommand("stop", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("stop: Too many arguments");
                        return -1;
                    }
                    if (!server.isStarted()) {
                        shell.writer.println("not started");
                        return -1;
                    }
                    int port;
                    try {
                        port = server.stop();
                    } catch (InterruptedException e) {
                        shell.writer.println("Critical error: main thread interrupted");
                        return -1;
                    }
                    shell.writer.println(String.format("stopped at %d", port));
                    return 0;
                }
            }),
            new Shell.ShellCommand("listusers", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("listusers: Too many arguments");
                        return -1;
                    }
                    if (!server.isStarted()) {
                        shell.writer.println("not started");
                        return -1;
                    }
                    for (String entry : server.listConnections()) {
                        shell.writer.println(entry);
                    }
                    return 0;
                }
            })
    };

    public void close() {
        try {
            if (remoteFactory != null) {
                remoteFactory.close();
            }
        } catch (Exception e) {
            //Nothing
        }
        if (server != null) {
            if (server.isStarted()) {
                try {
                    server.stop();
                } catch (InterruptedException e) {
                    //Exit function nothing to do
                }
            }
        }
        try {
            if (provider != null) {
                provider.close();
            }
        } catch (Exception e) {
            //Nothing
        }
    }

    public void integrate(Shell shell) {
        for (Shell.ShellCommand command : commands) {
            shell.addCommand(command);
        }
        shell.addExitFunction(new Shell.ShellCommand(null, (shell1, args) -> {
            close();
            return 0;
        }));
    }
}
