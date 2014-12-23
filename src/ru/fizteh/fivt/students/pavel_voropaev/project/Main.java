package ru.fizteh.fivt.students.pavel_voropaev.project;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.DatabaseFactory;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Command;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.Interpreter;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.database.*;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.table.*;

public class Main {
    public static void main(String[] args) {
        String databasePath = System.getProperty("fizteh.db.dir");
        if (databasePath == null) {
            System.err.println("You must specify fizteh.db.dir via -Dfizteh.db.dir JVM parameter");
            System.exit(-1);
        }

        TableProvider database = null;
        try {
            TableProviderFactory factory = new DatabaseFactory();
            database = factory.create(databasePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        DatabaseInterpreterState state = new DatabaseInterpreterState(database);
        Command[] commands = new Command[]{
                new Create(state), new Drop(state), new ShowTables(state), new Size(state), new Use(state),
                new Commit(state), new Get(state), new ListKeys(state), new Put(state), new Remove(state),
                new Rollback(state),
                new Exit(state)};

        Interpreter interpreter = new Interpreter(commands, state);
        try {
            interpreter.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
