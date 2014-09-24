package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.ListCommand;
import ru.fizteh.fivt.students.dsalnikov.filemap.commands.PutCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        Table t = new SingleFileTable();
        PutCommand pc = new PutCommand(t);
        ListCommand lc = new ListCommand(t);
        Shell sh = new Shell();
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(pc);
        commands.add(lc);
        sh.setCommands(commands);
        if (args.length == 0) {
            sh.batchMode();
        } else {
            sh.commandMode(args);
        }
    }
}
