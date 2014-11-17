package ru.fizteh.fivt.students.sautin1.shell;

/**
 * "pwd" command.
 * Created by sautin1 on 9/30/14.
 */
public class PwdCommand extends AbstractShellCommand {

    public PwdCommand() {
        super("pwd", 0, 0);
    }

    /**
     * Prints the present working directory.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        System.out.println(state.getPWD().toString());
    }

}
