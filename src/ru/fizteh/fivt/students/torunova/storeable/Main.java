package ru.fizteh.fivt.students.torunova.storeable;
import ru.fizteh.fivt.students.torunova.storeable.database.Database;
import ru.fizteh.fivt.students.torunova.storeable.database.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.actions.*;
import ru.fizteh.fivt.students.torunova.storeable.interpreter.Shell;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nastya on 22.10.14.
 */

public class Main {
    private static final String DATABASE_DIRECTORY = "fizteh.db.dir";
    public static void main(String[] args) {
        if (System.getProperty(DATABASE_DIRECTORY) == null) {
            System.err.println("Name of database not specified. Please, specify it via -D" + DATABASE_DIRECTORY);
            System.exit(1);
        }
        TableHolder currentTable = null;
        try {
            Database db = new Database(System.getProperty(DATABASE_DIRECTORY));
            currentTable = new TableHolder(new DatabaseWrapper(db.getDbName()));
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        Set<Action> actions = new HashSet<>();
        actions.add(new Put(currentTable));
        actions.add(new Get(currentTable));
        actions.add(new MyList(currentTable));
        actions.add(new Remove(currentTable));
        actions.add(new CreateTable(currentTable));
        actions.add(new DropTable(currentTable));
        actions.add(new UseTable(currentTable));
        actions.add(new ShowTables(currentTable));
        actions.add(new Commit(currentTable));
        actions.add(new Rollback(currentTable));
        actions.add(new Exit(currentTable));
        actions.add(new Size(currentTable));
        Shell shell;

        if (args.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(parseCommandsFromArray(args).getBytes());
            shell = new Shell(actions, is, System.out, currentTable, "exit", false);
        } else {
            shell = new Shell(actions, System.in, System.out, currentTable, "exit", true);
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
