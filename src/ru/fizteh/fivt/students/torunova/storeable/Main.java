package ru.fizteh.fivt.students.torunova.storeable;
import ru.fizteh.fivt.students.torunova.storeable.actions.*;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nastya on 22.10.14.
 */

public class Main {
    public static void main(String[] args) {
        Set<Action> actions = new HashSet<>();
        actions.add(new Put());
        actions.add(new Get());
        actions.add(new MyList());
        actions.add(new Remove());
        actions.add(new CreateTable());
        actions.add(new DropTable());
        actions.add(new UseTable());
        actions.add(new ShowTables());
        actions.add(new Commit());
        actions.add(new Rollback());
        actions.add(new Exit());
        actions.add(new Size());
        Shell shell;
        if (args.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(parseCommandsFromArray(args).getBytes());
            shell = new Shell(actions, is, System.getProperty("fizteh.db.dir"), false);
        } else {
            shell = new Shell(actions, System.in, System.getProperty("fizteh.db.dir"), true);
        }
        shell.run();
    }

    private static String parseCommandsFromArray(final String[] commands) {
        StringBuilder b = new StringBuilder();
        for (String command:commands) {
            b.append(command).append(" ");
        }
        return b.toString();
    }
}
