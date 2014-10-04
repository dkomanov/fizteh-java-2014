package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.*;

import java.util.ArrayList;

public class CommandMV extends Command {
    public Shell shellLink;

    public CommandMV(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing file operand");
        }

        if (args.size() == 1) {
            throw new Exception("missing destination file operand after" + "'" + args.get(1) + "'");
        }
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        CommandCP commandCP = new CommandCP(shellLink);
        CommandRM commandRM = new CommandRM(shellLink);

        args.add(0, "-r");
        commandCP.execute(args);

        args.remove(2);
        commandRM.execute(args);
    }

}
