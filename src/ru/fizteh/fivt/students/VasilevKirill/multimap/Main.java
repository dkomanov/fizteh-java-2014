package ru.fizteh.fivt.students.VasilevKirill.multimap;

import ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell.Shell;
import ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 19.10.2014.
 */

public class Main {
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
            try {
                int retValue = 0;
                String rootDirectory = System.getProperty("fizteh.db.dir");
                if (rootDirectory == null) {
                    throw new IOException("Can't find the directory");
                }
                MultiMap multiMap = new MultiMap(rootDirectory);
                Status status = new Status(multiMap);
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
}
