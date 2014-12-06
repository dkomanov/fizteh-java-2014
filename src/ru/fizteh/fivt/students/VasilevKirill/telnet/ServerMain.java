package ru.fizteh.fivt.students.VasilevKirill.telnet;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.*;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Shell;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProviderFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 03.12.2014.
 */
public class ServerMain {
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
            if (e.getMessage().equals("")) {
                System.out.println(e);
            } else {
                System.out.println(e.getMessage());
            }
            System.exit(-1);
        }//
    }
}

