package ru.fizteh.fivt.students.sautin1.junit.shell;

/**
 * A typical command class.
 * Created by sautin1 on 10/1/14.
 */
public abstract class AbstractCommand<T> implements Command<T> {
    public enum CheckArgumentNumber {
        LESS, MORE, EQUAL
    }

    private int minArgNumber;
    private int maxArgNumber;
    private String name;

    public AbstractCommand(String name, int minArgNumber, int maxArgNumber) {
        this.name = name;
        this.minArgNumber = minArgNumber;
        this.maxArgNumber = maxArgNumber;
    }

    /**
     * Returns the string which represents the name of the command.
     * @return name of the command.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns minimal argument number required by this command.
     * @return minArgNumber.
     */
    @Override
    public int getMinArgNumber() {
        return minArgNumber;
    }

    /**
     * Returns maximal argument number required by this command.
     * @return maxArgNumber.
     */
    @Override
    public int getMaxArgNumber() {
        return maxArgNumber;
    }

    @Override
    public abstract void execute(T state, String... args) throws UserInterruptException, CommandExecuteException;

    /**
     * Checks the number of arguments for a specific command.
     * @param args - array of strings, arguments of a command.
     * @return MORE, if too many arguments provided; LESS, if not enough arguments provided; EQUAL, otherwise.
     */
    public CheckArgumentNumber checkArgumentNumber(String... args) {
        CheckArgumentNumber result;
        if (args.length > maxArgNumber + 1) {
            result = CheckArgumentNumber.MORE;
        } else if (args.length < minArgNumber + 1) {
            result = CheckArgumentNumber.LESS;
        } else {
            result = CheckArgumentNumber.EQUAL;
        }
        return result;
    }
}
