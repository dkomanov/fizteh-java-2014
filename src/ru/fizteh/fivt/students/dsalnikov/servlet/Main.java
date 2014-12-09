package ru.fizteh.fivt.students.dsalnikov.servlet;


import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.servlet.commands.StartCommand;
import ru.fizteh.fivt.students.dsalnikov.servlet.commands.StopCommand;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;
import ru.fizteh.fivt.students.dsalnikov.servlet.server.ServletServer;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String workingDirectory = System.getProperty("fizteh.db.dir");
        if (workingDirectory == null) {
            System.err.println("Data Base directory is not set");
            System.exit(3);
        }
        try {
            StorableTableProviderFactory factory = new StorableTableProviderFactory();
            TableProvider provider = factory.create(workingDirectory);
            //    Database state = new Database(workingFile, provider);
            TransactionManager manager = new TransactionManager(provider);
            ServletServer server = new ServletServer(manager);
            Shell sh = new Shell(new StorableParser());
            List<Command> commands = new ArrayList<>();
            commands.add(new StartCommand(server));
            commands.add(new StopCommand(server));
            sh.setCommands(commands);
            if (args.length == 0) {
                try {
                    sh.interactiveMode();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } else {
                try {
                    sh.batchMode(args);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
