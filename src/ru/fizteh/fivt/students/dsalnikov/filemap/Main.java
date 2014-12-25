package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StringParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            File dbfile = new File(System.getProperty("db.file"));
            Table t = new SingleFileTable(dbfile);
            Shell sh = new Shell(new StringParser());
            List<Command> commands = new ArrayList<>();
            commands.add(new PutCommand(t));
            commands.add(new ListCommand(t));
            commands.add(new RemoveCommand(t));
            commands.add(new GetCommand(t));
            commands.add(new ExitCommand(t));
            commands.add(new CommitCommand(t));
            commands.add(new RollbackCommand(t));
            commands.add(new SizeCommand(t));
            sh.setCommands(commands);
            try {
                if (args.length == 0) {
                    sh.interactiveMode();
                } else {
                    sh.batchMode(args);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }

        } catch (Throwable thr) {
            System.err.println(thr.getMessage());
            System.exit(1);
        }
    }
}
