package ru.fizteh.fivt.students.irina_karatsapova.filemap;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.commands.ExitCommand;
import ru.fizteh.fivt.students.irina_karatsapova.filemap.commands.GetCommand;
import ru.fizteh.fivt.students.irina_karatsapova.filemap.commands.PutCommand;
import ru.fizteh.fivt.students.irina_karatsapova.filemap.commands.RemoveCommand;
import ru.fizteh.fivt.students.irina_karatsapova.filemap.database.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.filemap.utils.Utils;

public class Main {
    public static String mainDir = "db.file";

    public static void main(String[] args) {
        //System.setProperty("db.file", "D:/tmp/db2.txt");

        DataBase.init();
        Shell shell = new Shell();
        shell.addCommand(new PutCommand());
        shell.addCommand(new GetCommand());
        shell.addCommand(new RemoveCommand());
        shell.addCommand(new ExitCommand());
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            String wholeArgument = Utils.concatStrings(args, " ");
            try {
                shell.batchMode(wholeArgument);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
