package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.io.File;
import java.util.ArrayList;

public class ShellBigBoss {
    private final FileUsing database;

    public ShellBigBoss(final String location) {
        database = new FileUsing(new File(location));
    }

    private final Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
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
                    String value = database.put(args.get(0), args.get(1));
                    if (value == null) {
                        System.out.println("new");
                    } else {
                        System.out.printf("overwrite\n%s\n", value);
                    }
                    database.writeToDisk();
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
                    String value = database.get(args.get(0));
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
                    if (database.remove(args.get(0)) != null) {
                        System.out.println("removed");
                        database.writeToDisk();
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
                    for (String key : database.getKeysList()) {
                        System.out.println(key);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("exit", new Shell.ShellExecutable() {
                @Override
                public int execute(final Shell shell, final ArrayList<String> args) {
                    shell.stop();
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
