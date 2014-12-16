package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import ru.fizteh.fivt.students.elina_denisova.shell.Shell;

import java.util.ArrayList;


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
