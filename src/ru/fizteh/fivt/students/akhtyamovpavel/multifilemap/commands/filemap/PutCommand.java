package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class PutCommand implements Command {
    private DataBaseShell shell;

    public PutCommand(DataBaseShell shell) {
        this.shell = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {

        if (arguments.size() != 2) {
            throw new Exception("usage: put key value");
        }
        if (shell.getFileMap() == null) {
            System.out.println("no table");
            return;
        }
        if (shell.getFileMap().containsKey(arguments.get(0))) {
            System.out.println("overwrite");
            System.out.println(shell.getFileMap().get(arguments.get(0)));
        } else {
            System.out.println("new");
            shell.putKeyToTable(shell.getOpenedTableName());
        }
        shell.getFileMap().put(arguments.get(0), arguments.get(1));

    }

    @Override
    public String getName() {
        return "put";
    }
}
