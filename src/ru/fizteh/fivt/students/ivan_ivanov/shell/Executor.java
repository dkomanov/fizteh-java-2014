package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Executor {

    public Map<String, Command> mapOfCmd = new HashMap<String, Command>();

    final String[] argsCheck(final String inCommand) {

        int space = inCommand.indexOf(" ");
        if (-1 == space) {
            return new String[0];
        }
        String substr = inCommand.substring(space + 1);
        return substr.trim().split("\\ ");
    }

    final String cmdCheck(final String cmd) {

        String tmp;
        int space = cmd.indexOf(" ");
        if (-1 == space) {
            space = cmd.length();
        }
        tmp = cmd.substring(0, space);
        return tmp;
    }

    abstract void list();

    final void execute(final Shell shell, final String cmd) throws IOException {

        if (!mapOfCmd.containsKey(cmdCheck(cmd))) {
            throw new IOException("Can't find key");
        }
        mapOfCmd.get(cmdCheck(cmd)).executeCmd(shell, argsCheck(cmd));
    }
}
