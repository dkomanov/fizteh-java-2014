package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandPwd extends Command {

    public CommandPwd() {
        minArgNumber = 0;
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments()) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
    }

    @Override
    public String toString() {
        return "pwd";
    }
}
