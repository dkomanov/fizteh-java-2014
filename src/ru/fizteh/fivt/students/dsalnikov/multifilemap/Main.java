package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.ShowCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        MultiTable t = new MultiFileHashMap();
        PutCommand pc = new PutCommand(t);
        ListCommand lc = new ListCommand(t);
        RemoveCommand rc = new RemoveCommand(t);
        GetCommand gc = new GetCommand(t);
        ExitCommand ec = new ExitCommand(t);
        CreateCommand cc = new CreateCommand(t);
        DropCommand dc = new DropCommand(t);
        ShowCommand sc = new ShowCommand(t);
        UseCommand uc = new UseCommand(t);
        CommitCommand com = new CommitCommand(t);
        RollbackCommand rolb = new RollbackCommand(t);
        SizeCommand siz = new SizeCommand(t);
        Shell sh = new Shell();
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(pc);
        commands.add(lc);
        commands.add(rc);
        commands.add(gc);
        commands.add(ec);
        commands.add(cc);
        commands.add(dc);
        commands.add(sc);
        commands.add(uc);
        commands.add(com);
        commands.add(rolb);
        commands.add(siz);
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
