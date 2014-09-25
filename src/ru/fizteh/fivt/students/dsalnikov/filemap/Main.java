package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Table t = new SingleFileTable();
        PutCommand pc = new PutCommand(t);
        ListCommand lc = new ListCommand(t);
        RemoveCommand rc = new RemoveCommand(t);
        GetCommand gc = new GetCommand(t);
        ExitCommand ec = new ExitCommand(t);
        Shell sh = new Shell();
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(pc);
        commands.add(lc);
        commands.add(rc);
        commands.add(gc);
        commands.add(ec);
        sh.setCommands(commands);
        if (args.length == 0) {
            try {
                sh.batchMode();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            try {
                sh.commandMode(args);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
