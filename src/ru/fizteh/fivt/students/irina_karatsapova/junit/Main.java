package ru.fizteh.fivt.students.irina_karatsapova.junit;

import ru.fizteh.fivt.students.irina_karatsapova.junit.commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.MyTableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

public class Main {
    public static String mainDir = "fizteh.db.dir";

    public static void main(String[] args) {
        //System.setProperty(mainDir, "D:/tmp/db5");

        TableProviderFactory tableProviderFactory = new MyTableProviderFactory();
        if (System.getProperty(Main.mainDir) == null) {
            System.err.println("Path to the database is not set up. Use -D" + Main.mainDir + "=...");
            System.exit(1);
        }
        TableProvider tableProvider = tableProviderFactory.create(System.getProperty(Main.mainDir));

        Interpreter interpreter = new Interpreter();
        interpreter.addCommand(new PutCommand());
        interpreter.addCommand(new GetCommand());
        interpreter.addCommand(new RemoveCommand());
        interpreter.addCommand(new ExitCommand());
        interpreter.addCommand(new CreateCommand());
        interpreter.addCommand(new DropCommand());
        interpreter.addCommand(new UseCommand());
        interpreter.addCommand(new ShowCommand());
        interpreter.addCommand(new SizeCommand());
        interpreter.addCommand(new CommitCommand());
        interpreter.addCommand(new RollbackCommand());
        if (args.length == 0) {
            interpreter.interactiveMode(tableProvider);
        } else {
            String wholeArgument = Utils.concatStrings(args, " ");
            try {
                interpreter.batchMode(tableProvider, wholeArgument);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
