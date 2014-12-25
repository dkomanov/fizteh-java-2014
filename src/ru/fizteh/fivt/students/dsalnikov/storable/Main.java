package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.ShowCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String workingDirectory = System.getProperty("fizteh.db.dir");
        if (workingDirectory == null) {
            System.err.println("Data Base directory is not set");
            System.exit(3);
        }
        try {
            StorableTableProviderFactory factory = new StorableTableProviderFactory();
            Database state = new Database(new File(workingDirectory), factory.create(workingDirectory));
            Shell sh = new Shell(new StorableParser());
            List<Command> commands = new ArrayList<>();
            commands.add(new PutCommand(state));
            commands.add(new ListCommand(state));
            commands.add(new RemoveCommand(state));
            commands.add(new GetCommand(state));
            commands.add(new ExitCommand(state));
            commands.add(new CreateCommand(state));
            commands.add(new DropCommand(state));
            commands.add(new UseCommand(state));
            commands.add(new CommitCommand(state));
            commands.add(new RollbackCommand(state));
            commands.add(new SizeCommand(state));
            commands.add(new ShowCommand(state));
            commands.add(new ListCommand(state));
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
