package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.filemap;


import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class GetCommand implements Command {
    private DataBaseShell shell;

    public GetCommand(DataBaseShell shell) {
        this.shell = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: get key");
        }
        if (shell.getFileMap() == null) {
            System.out.println("no table");
            return;
        }

        if (shell.getFileMap().containsKey(arguments.get(0))) {
            System.out.println("found");
            System.out.println(shell.getFileMap().get(arguments.get(0)));
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "get";
    }
}
