package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

import ru.fizteh.fivt.students.Oktosha.Command.Command;

/**
 * ConsoleUtility is a class which should be extended by Shell, DbMain or
 * whatever else which represents a set of console commands.
 */
public abstract class ConsoleUtility {

    public void run(Command cmd) throws CommandDoesNotExist,
                                        ArgumentSyntaxException {
        switch (cmd.name) {
            case "exit":
                exit(cmd.args);
                break;
            case "":
                break;
            default:
                throw new CommandDoesNotExist(cmd.name + ": doesn't exist");
        }
    }

    protected void exit(String[] args) throws ArgumentSyntaxException {
        if (args.length != 0) {
            throw new ArgumentSyntaxException("exit: too many arguments");
        }
        System.exit(0);
    }
}
