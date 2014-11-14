package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.InvocationException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.WrongArgsNumberException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.AccurateExceptionHandler;

import java.text.ParseException;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility.*;

/**
 * Convenience class for Commands.
 * @author phoenix
 */
public abstract class AbstractCommand implements Command<SingleDatabaseShellState> {
    private static final Class<?>[] EXECUTE_SAFELY_THROWN_EXCEPTIONS =
            obtainExceptionsThrownByExecuteSafely();
    /**
     * Used for unsafe calls. Catches and handles all exceptions thrown by {@link
     * ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.AbstractCommand#executeSafely
     * (SingleDatabaseShellState,
     * String[])}.
     * } and {@link
     * IllegalArgumentException }.
     */
    protected static final AccurateExceptionHandler<SingleDatabaseShellState> DATABASE_ERROR_HANDLER =
            (Exception exc, SingleDatabaseShellState shell) -> {
                boolean found = false;
                Class<?> excClass = exc.getClass();

                for (Class<?> exceptionType : EXECUTE_SAFELY_THROWN_EXCEPTIONS) {
                    try {
                        exceptionType.asSubclass(excClass);
                        found = true;
                        break;
                    } catch (ClassCastException cce) {
                        // Ignore it.
                    }
                }

                if (found) {
                    handleError(exc.getMessage(), exc, true);
                } else if (exc instanceof RuntimeException) {
                    throw (RuntimeException) exc;
                } else {
                    throw new RuntimeException("Unexpected exception", exc);
                }
            };
    private final String info;
    private final String invocationArgs;
    private final int minimalArgsCount;
    private final int maximalArgsCount;

    /**
     * @param invocationArgs
     *         Sequence of arguments that can be mentioned, e.g. ' {@code <key> <value>}'
     * @param info
     *         Short description of command.
     */
    public AbstractCommand(String invocationArgs, String info, int minimalArgsCount, int maximalArgsCount) {
        this.info = info;
        this.invocationArgs = invocationArgs;
        this.minimalArgsCount = minimalArgsCount;
        this.maximalArgsCount = maximalArgsCount;
    }

    public AbstractCommand(String invocationArgs, String info, int expectedArgsCount) {
        this(invocationArgs, info, expectedArgsCount, expectedArgsCount);
    }

    private static Class<?>[] obtainExceptionsThrownByExecuteSafely() {
        Class<?>[] exceptions;

        try {
            exceptions = AbstractCommand.class
                    .getMethod("executeSafely", SingleDatabaseShellState.class, String[].class)
                    .getExceptionTypes();
        } catch (Exception exc) {
            exceptions = null;
        }
        return exceptions;
    }

    /**
     * In implementation of {@link AbstractCommand} arguments number is checked first and then
     * {@link #executeSafely(SingleDatabaseShellState, String[])} is invoked.<br/> If you want to
     * disable forced arguments number checking, override this method without invocation super
     * method and put empty implementation inside {@link #executeSafely(SingleDatabaseShellState,
     * String[])}.
     */
    @Override
    public void execute(final SingleDatabaseShellState state, final String[] args) throws TerminalException {
        checkArgsNumber(args, minimalArgsCount, maximalArgsCount);
        performAccurately(() -> executeSafely(state, args), DATABASE_ERROR_HANDLER, state);
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getInvocation() {
        return invocationArgs;
    }

    public abstract void executeSafely(SingleDatabaseShellState shell, String[] args) throws
                                                                                      DatabaseIOException,
                                                                                      IllegalArgumentException,
                                                                                      NoActiveTableException,
                                                                                      IllegalStateException,
                                                                                      InvocationException,
                                                                                      ParseException;

    protected void checkArgsNumber(String[] args, int minimal, int maximal) throws TerminalException {
        if (args.length < minimal || args.length > maximal) {
            handleError(null, new WrongArgsNumberException(this, args[0]), true);
        }
    }
}
