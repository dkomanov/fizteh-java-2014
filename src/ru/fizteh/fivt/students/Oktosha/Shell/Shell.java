package ru.fizteh.fivt.students.Oktosha.Shell;

import ru.fizteh.fivt.students.Oktosha.Command.Command;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ArgumentSyntaxException;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.CommandIsNotSupportedException;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;
import ru.fizteh.fivt.students.Oktosha.Executor.InteractiveExecutor;
import ru.fizteh.fivt.students.Oktosha.Executor.PackageExecutor;

public class Shell implements ConsoleUtility {

    public static void main(String[] args) {
        if (args.length == 0) {
            InteractiveExecutor.execute(new Shell());
        } else {
            PackageExecutor.execute(new Shell(), args);
        }
    }

    public void run(Command cmd) throws CommandIsNotSupportedException,
                                        ArgumentSyntaxException {
        switch (cmd.name) {
            case "exit":
                exit(cmd.args);
                break;
            case "":
                break;
            default:
                throw new CommandIsNotSupportedException(cmd.name + ": command isn't supported");
        }
    }

    private void exit(String[] args) throws ArgumentSyntaxException {
        if (args.length != 0) {
            throw new ArgumentSyntaxException("exit: too many arguments");
        }
        System.exit(0);
    }
}
