package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.WrongArgsNumberException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.AccurateAction;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.AccurateExceptionHandler;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility.*;

/**
 * Convenience class for Commands.
 * @author phoenix
 */
public abstract class AbstractCommand implements Command<SingleDatabaseShellState> {
    /**
     * Used for unsafe calls. Catches all extensions of {@link DatabaseException } and {@link
     * IllegalArgumentException }.
     */
    protected static final AccurateExceptionHandler<SingleDatabaseShellState>
            DATABASE_ERROR_HANDLER = new AccurateExceptionHandler<SingleDatabaseShellState>() {

        @Override
        public void handleException(Exception exc, SingleDatabaseShellState shell)
                throws TerminalException {
            if (exc instanceof DatabaseException || exc instanceof IllegalArgumentException) {
                Utility.handleError(exc.getMessage(), exc, true);
            } else if (exc instanceof RuntimeException) {
                throw (RuntimeException) exc;
            } else {
                throw new RuntimeException("Unexpected exception", exc);
            }
        }

    };

    private String info;
    private String invocationArgs;

    private int minimalArgsCount;
    private int maximalArgsCount;

    /**
     * @param invocationArgs
     *         Sequence of arguments that can be mentioned, e.g. ' {@code <key> <value>}'
     * @param info
     *         Short description of command.
     */
    public AbstractCommand(String invocationArgs,
                           String info,
                           int minimalArgsCount,
                           int maximalArgsCount) {
        this.info = info;
        this.invocationArgs = invocationArgs;
        this.minimalArgsCount = minimalArgsCount;
        this.maximalArgsCount = maximalArgsCount;
    }

    public AbstractCommand(String invocationArgs, String info, int expectedArgsCount) {
        this(invocationArgs, info, expectedArgsCount, expectedArgsCount);
    }

    /**
     * In implementation of {@link AbstractCommand} arguments number is checked first and then
     * {@link #executeSafely(SingleDatabaseShellState, String[])} is invoked.<br/> If you want to
     * disable forced arguments number checking, override this method without invocation super
     * method and put empty implementation inside {@link #executeSafely(SingleDatabaseShellState,
     * String[])}.
     */
    @Override
    public void execute(final SingleDatabaseShellState state, final String[] args)
            throws TerminalException {
        checkArgsNumber(args, minimalArgsCount, maximalArgsCount);
        Utility.performAccurately(
                new AccurateAction() {

                    @Override
                    public void perform() throws Exception {
                        executeSafely(state, args);
                    }
                }, DATABASE_ERROR_HANDLER, state);
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getInvocation() {
        return invocationArgs;
    }

    public abstract void executeSafely(SingleDatabaseShellState shell, String[] args)
            throws DatabaseException, IllegalArgumentException;

    protected void checkArgsNumber(String[] args, int minimal, int maximal)
            throws TerminalException {
        if (args.length < minimal || args.length > maximal) {
            handleError(null, new WrongArgsNumberException(this, args[0]), true);
        }
    }

    protected void checkArgsNumber(String[] args, int expected) throws TerminalException {
        checkArgsNumber(args, expected, expected);
    }
}
