package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class MoveCommand extends FileCommand {
    public MoveCommand(Shell shell) {
        link = shell;
    }

    @Override
    protected void checkArgumentNumberCorrection(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage <source> <destination>");
        }
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        checkArgumentNumberCorrection(arguments);
        ArrayList<String> newArguments = new ArrayList<String>();
        newArguments.add("-r");
        newArguments.add(arguments.get(0));
        newArguments.add(arguments.get(1));

        (new CopyCommand(link)).executeCommand(newArguments);

        newArguments.remove(2);
        (new RemoveCommand(link)).executeCommand(newArguments);
    }

    @Override
    public String getName() {
        return "mv";
    }


}
