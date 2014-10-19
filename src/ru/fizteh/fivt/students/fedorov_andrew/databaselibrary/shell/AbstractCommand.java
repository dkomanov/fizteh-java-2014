package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import static ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility.*;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.HandledException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.WrongArgsNumberException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.AccurateAction;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.AccurateExceptionHandler;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

/**
 * Convenience class for Commands.
 * 
 * @author phoenix
 * 
 */
public abstract class AbstractCommand implements Command {
    /**
     * Used for unsafe calls. Catches all extensions of
     * {@link DatabaseException } and {@link IllegalArgumentException }.
     */
    protected final static AccurateExceptionHandler<Shell> databaseErrorHandler = new AccurateExceptionHandler<Shell>() {

	@Override
	public void handleException(Exception exc, Shell shell) {
	    if (exc instanceof DatabaseException
		    || exc instanceof IllegalArgumentException) {
		handleError(exc, exc.getMessage(), true);
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
     *            Sequence of arguments that can be mentioned, e.g. '
     *            {@code <key> <value>}'
     * @param info
     *            Short description of command.
     */
    public AbstractCommand(String invocationArgs, String info,
	    int minimalArgsCount, int maximalArgsCount) {
	this.info = info;
	this.invocationArgs = invocationArgs;
	this.minimalArgsCount = minimalArgsCount;
	this.maximalArgsCount = maximalArgsCount;
    }

    public AbstractCommand(String invocationArgs, String info,
	    int expectedArgsCount) {
	this(invocationArgs, info, expectedArgsCount, expectedArgsCount);
    }

    /**
     * In implementation of {@link AbstractCommand} arguments number is checked
     * first and then {@link #executeSafely(Shell, String[])} is
     * invoked.<br/>
     * If you want to disable forced arguments number checking, override this
     * method without invocation super method and put empty implementation
     * inside {@link #executeSafely(Shell, String[])}.
     */
    @Override
    public void execute(final Shell shell, final String[] args)
	    throws HandledException {
	checkArgsNumber(args, minimalArgsCount, maximalArgsCount);
	Utility.performAccurately(new AccurateAction() {

	    @Override
	    public void perform() throws Exception {
		executeSafely(shell, args);
	    }
	}, databaseErrorHandler, shell);
    }

    public abstract void executeSafely(Shell shell, String[] args)
	    throws DatabaseException, IllegalArgumentException;

    protected void checkArgsNumber(String[] args, int minimal, int maximal) {
	if (args.length < minimal || args.length > maximal) {
	    handleError(new WrongArgsNumberException(this), null, true);
	}
    }

    protected void checkArgsNumber(String[] args, int expected) {
	checkArgsNumber(args, expected, expected);
    }

    @Override
    public String getInvocation() {
	return invocationArgs;
    }

    @Override
    public String getInfo() {
	return info;
    }
}
