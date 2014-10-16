package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        DataBase.init(new File(Utils.mainDir()));

        Table.initKeysArray();

        Shell shell = new Shell();
        shell.addCommand(new PutCommand());
        shell.addCommand(new GetCommand());
        shell.addCommand(new RemoveCommand());
        shell.addCommand(new ExitCommand());
        shell.addCommand(new CreateCommand());
        shell.addCommand(new DropCommand());
        shell.addCommand(new UseCommand());
        shell.addCommand(new ShowCommand());
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            String wholeArgument = Utils.concatStrings(args, " ");
            try {
                shell.batchMode(wholeArgument);
                SaveTable.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
