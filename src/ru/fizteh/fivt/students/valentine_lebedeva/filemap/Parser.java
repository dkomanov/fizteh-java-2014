package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ListCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.PutCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ExitCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.GetCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.RemoveCmd;

public final class Parser {
    private Parser() {
         //not called only for checkstyle
    }

    public static void parse(final String cmdArgs,
            final Boolean cmdMode, final DB dataBase) throws Exception {
        try {
            String[] parseArgs = cmdArgs.split(" ");
            switch (parseArgs[0]) {
            case "put":
                PutCmd putCommand = new PutCmd();
                putCommand.execute(dataBase, parseArgs);
                break;
            case "get":
                GetCmd getCommand = new GetCmd();
                getCommand.execute(dataBase, parseArgs);
                break;
            case "remove":
                RemoveCmd removeCommand = new RemoveCmd();
                removeCommand.execute(dataBase, parseArgs);
                break;
            case "list":
                ListCmd listCommand = new ListCmd();
                listCommand.execute(dataBase, parseArgs);
                break;
            case "exit":
                ExitCmd exitCommand = new ExitCmd();
                exitCommand.execute(dataBase, parseArgs);
            default:
                throw new Exception("Incorrect arguments");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (cmdMode) {
                System.exit(1);
            }
        }
    }
}
