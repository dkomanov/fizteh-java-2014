package ru.fizteh.fivt.students.Oktosha.Executor;

import ru.fizteh.fivt.students.Oktosha.Command.Command;


/**
 * A class which executes an instance of ConsoleUtility
 */
public abstract class Executor {
    protected Command[] parse(String s) throws ExecutorParseException {
        if (s.matches(".*;\\s*;.*")) {
            throw new ExecutorParseException();
        }
        String[] commandStrings = s.split(";");
        Command[] res = new Command[commandStrings.length];
        for (int i = 0; i < res.length; ++i) {
            String[] split = commandStrings[i].split("\\s+");
            res[i].name = split[0];
            System.arraycopy(split, 1, res[i].args, 0, split.length - 1);
        }
        return res;
    }
}
