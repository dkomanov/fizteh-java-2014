package ru.fizteh.fivt.students.torunova.parallel;
import ru.fizteh.fivt.students.torunova.parallel.actions.*;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nastya on 22.10.14.
 */

public class Main {
    private static final String DATABASE_DIRECTORY = "fizteh.db.dir";
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
        if (System.getProperty(DATABASE_DIRECTORY) == null) {
            System.err.println("Name of database not specified.Please,specify it via -D" + DATABASE_DIRECTORY);
            System.exit(1);
        }
        if (args.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(parseCommandsFromArray(args).getBytes());
            shell = new Shell(actions, is, System.getProperty(DATABASE_DIRECTORY), false);
        } else {
            shell = new Shell(actions, System.in, System.getProperty(DATABASE_DIRECTORY), true);
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
