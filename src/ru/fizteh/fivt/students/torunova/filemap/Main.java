package ru.fizteh.fivt.students.torunova.filemap;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nastya on 09.10.14.
 */
public class Main {
    public static void main(String[] args) {
        Set<Action> actions = new HashSet<Action>();
        Put put = new Put();
        List list = new List();
        Get get = new Get();
        Remove rm = new Remove();
        actions.add(put);
        actions.add(get);
        actions.add(list);
        actions.add(rm);
        Shell shell;
        if (args.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(parseCommandsFromArray(args).getBytes());
            shell = new Shell(actions, is, System.getProperty("db.file"), false);
        } else {
            shell = new Shell(actions, System.in, System.getProperty("db.file"), true);
        }
        shell.run();
    }

    private static String parseCommandsFromArray(final String[] commands) {
        StringBuilder b = new StringBuilder();
        int length = commands.length;
        for (int i = 0; i < length; i++) {
            b.append(commands[i]).append(" ");
        }
        String functions = b.toString();
        return functions;
    }
}
