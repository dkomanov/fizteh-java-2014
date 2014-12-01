package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand implements Command {
    private DataBaseShell shell;

    public ListCommand(DataBaseShell shell) {
        this.shell = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {

        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        if (shell.getFileMap() == null) {
            System.out.println("no table");
            return;
        }

        for (String currentKey : shell.getFileMap().keySet()) {
            System.out.print(currentKey + " ");
        }
        System.out.println();
    }

    @Override
    public String getName() {
        return "list";
    }
}
