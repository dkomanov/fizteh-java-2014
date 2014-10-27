package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            File dbfile = new File(System.getProperty("db.file"));
            Table t = new SingleFileTable(dbfile);
            PutCommand pc = new PutCommand(t);
            ListCommand lc = new ListCommand(t);
            RemoveCommand rc = new RemoveCommand(t);
            GetCommand gc = new GetCommand(t);
            ExitCommand ec = new ExitCommand(t);
            CommitCommand cc = new CommitCommand(t);
            RollbackCommand rbc = new RollbackCommand(t);
            SizeCommand sz = new SizeCommand(t);
            Shell sh = new Shell();
            List<Command> commands = new ArrayList<>();
            commands.add(pc);
            commands.add(lc);
            commands.add(rc);
            commands.add(gc);
            commands.add(ec);
            commands.add(cc);
            commands.add(rbc);
            commands.add(sz);
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
