package ru.fizteh.fivt.students.kotsurba.filemap.shell;

import java.util.ArrayList;

public class Shell {
    private ArrayList<ShellCommand> allCommands;

    public Shell() {
        allCommands = new ArrayList<ShellCommand>();
    }

    public final void addCommand(final ShellCommand command) {
        allCommands.add(command);
    }

    public final void executeCommand(final CommandString command) {
        for (int i = 0; i < allCommands.size(); ++i) {
            if (allCommands.get(i).isMyCommand(command)) {
                allCommands.get(i).run();
                return;
            }
        }
        if ((command.length() > 0) && (!command.getArg(0).equals(""))) {
            throw new InvalidCommandException(command.getArg(0));
        }
    }
}
