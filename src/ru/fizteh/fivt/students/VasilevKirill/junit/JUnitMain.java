package ru.fizteh.fivt.students.VasilevKirill.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.*;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Shell;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 09.11.2014.
 */
public class JUnitMain {
    public static void main(String[] args) {
        try {
            Map<String, Command> commands = new HashMap<String, Command>();
            commands.put(new CreateCommand().toString(), new CreateCommand());
            commands.put(new DropCommand().toString(), new DropCommand());
            commands.put(new ShowTablesCommand().toString(), new ShowTablesCommand());
            commands.put(new UseCommand().toString(), new UseCommand());
            commands.put("put", new HandleTable());
            commands.put("get", new HandleTable());
            commands.put("list", new HandleTable());
            commands.put("remove", new HandleTable());
            commands.put(new CommitCommand().toString(), new CommitCommand());
            commands.put(new RollbackCommand().toString(), new RollbackCommand());
            commands.put(new SizeCommand().toString(), new SizeCommand());
            try {
                int retValue = 0;
                String rootDirectory = System.getProperty("fizteh.db.dir");
                if (rootDirectory == null) {
                    throw new IOException("Can't find the directory");
                }
                TableProvider dataBase = new MyTableProviderFactory().create(rootDirectory);
                Status status = new Status(dataBase);
                if (args.length == 0) {
                    new Shell(commands, status).handle(System.in);
                } else {
                    retValue = new Shell(commands, status).handle(args);
                }
                System.exit(retValue);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    //
}
