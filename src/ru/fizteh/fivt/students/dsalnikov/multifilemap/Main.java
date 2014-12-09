package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.ShowCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StringParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MultiTable t = new MultiFileHashMap();
        Shell sh = new Shell(new StringParser());
        List<Command> commands = new ArrayList<>();
        commands.add(new PutCommand(t));
        commands.add(new ListCommand(t));
        commands.add(new RemoveCommand(t));
        commands.add(new GetCommand(t));
        commands.add(new ExitCommand(t));
        commands.add(new CreateCommand(t));
        commands.add(new DropCommand(t));
        commands.add(new ShowCommand(t));
        commands.add(new UseCommand(t));
        commands.add(new CommitCommand(t));
        commands.add(new RollbackCommand(t));
        commands.add(new SizeCommand(t));
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
    }
}
