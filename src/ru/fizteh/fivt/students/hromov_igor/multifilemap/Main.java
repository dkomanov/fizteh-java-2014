package ru.fizteh.fivt.students.hromov_igor.multifilemap;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProvider;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBProviderFactory;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.commands.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.BaseCommand;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.BatchInterpreter;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.PackageParser;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

    try {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.print("Can't open directory");
        }

        DBProviderFactory factory = new DBProviderFactory();
        DBProvider base = (DBProvider) factory.create(path);

        DBaseTable table = null;
        CommandState state = new CommandState(base, table);

        HashMap<String, BaseCommand> listCommands = new HashMap<>();
        listCommands.put("create", new Create(state));
        listCommands.put("drop", new Drop(state));
        listCommands.put("use", new Use(state));
        listCommands.put("show_tables", new ShowTables(state));
        listCommands.put("put", new Put(state));
        listCommands.put("get", new Get(state));
        listCommands.put("remove", new Remove(state));
        listCommands.put("list", new List(state));
        listCommands.put("exit", new Exit(state));
        listCommands.put("commit", new Commit(state));
        listCommands.put("rollback", new Rollback(state));
        listCommands.put("size", new Size(state));

        if (args.length == 0) {
            BatchInterpreter.run(listCommands);
        } else {
            PackageParser.run(listCommands, args);
        }
    } catch (IllegalArgumentException e) {
        System.err.println(e);
    }
    }
}
