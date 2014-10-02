package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;

/**
 * "exit" command.
 * Created by sautin1 on 9/30/14.
 */
public class CommandExit extends Command {

    public CommandExit() {
        minArgNumber = 0;
        commandName = "exit";
    }

    /**
     * Does nothing.
     * @param args [0] - command name.
     * @throws IOException
     */
    @Override
    public void execute(String... args) throws IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
    }

}
