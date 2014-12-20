package ru.fizteh.fivt.students.torunova.proxy;

import ru.fizteh.fivt.students.torunova.proxy.database.Database;
import ru.fizteh.fivt.students.torunova.proxy.database.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.proxy.database.TableHolder;
import ru.fizteh.fivt.students.torunova.proxy.database.actions.*;
import ru.fizteh.fivt.students.torunova.proxy.interpreter.Shell;

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
        actions.add(new Put(currentTable, System.out));
        actions.add(new Get(currentTable, System.out));
        actions.add(new MyList(currentTable, System.out));
        actions.add(new Remove(currentTable, System.out));
        actions.add(new CreateTable(currentTable, System.out));
        actions.add(new DropTable(currentTable, System.out));
        actions.add(new UseTable(currentTable, System.out));
        actions.add(new ShowTables(currentTable, System.out));
        actions.add(new Commit(currentTable, System.out));
        actions.add(new Rollback(currentTable, System.out));
        actions.add(new Exit(currentTable));
        actions.add(new Size(currentTable, System.out));
        Shell shell;

        if (args.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(parseCommandsFromArray(args).getBytes());
            shell = new Shell(actions, is, System.out, "exit", false);
        } else {
            shell = new Shell(actions, System.in, System.out, "exit", true);
        }
        if (!shell.run()) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static String parseCommandsFromArray(final String[] commands) {
        StringBuilder b = new StringBuilder();
        for (String command:commands) {
            b.append(command).append(" ");
        }
        return b.toString();
    }
}

