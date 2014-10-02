package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;

/**
 * "pwd" command.
 * Created by sautin1 on 9/30/14.
 */
public class CommandPwd extends Command {

    public CommandPwd() {
        minArgNumber = 0;
        commandName = "pwd";
    }

    /**
     * Prints the present working directory.
     * @param args [0] - command name.
     * @throws IOException
     */
    @Override
    public void execute(String... args) throws IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        System.out.println(presentWorkingDirectory.toString());
    }

}
