package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import static ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Utility.*;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.TerminalException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.WrongArgsNumberException;

/**
 * Convenience class for Commands.
 * 
 * @author phoenix
 * 
 */
public abstract class AbstractCommand implements Command {
    private String info;
    private String invocationArgs;

    private int minimalArgsCount;
    private int maximalArgsCount;

    public AbstractCommand(String invocationArgs, String info,
	    int expectedArgsCount) {
	this(invocationArgs, info, expectedArgsCount, expectedArgsCount);
    }

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

    protected void checkArgsNumber(String[] args, int expected)
	    throws TerminalException {
	checkArgsNumber(args, expected, expected);
    }

    protected void checkArgsNumber(String[] args, int minimal, int maximal)
	    throws TerminalException {
	if (args.length < minimal || args.length > maximal) {
	    handleError(new WrongArgsNumberException(this), null, true);
	}
    }

    /**
     * In implementation of {@link AbstractCommand} arguments number is checked
     * first and then {@link #executeAfterChecking(Shell, String[])} is
     * invoked.<br/>
     * If you want to disable forced arguments number checking, override this
     * method without invocation super method and put empty implementation
     * inside {@link #executeAfterChecking(Shell, String[])}.
     */
    @Override
    public void execute(Shell shell, String[] args) throws TerminalException {
	checkArgsNumber(args, minimalArgsCount, maximalArgsCount);
	executeAfterChecking(shell, args);
    }

    public abstract void executeAfterChecking(Shell shell, String[] args)
	    throws TerminalException;

    @Override
    public String getInfo() {
	return info;
    }

    @Override
    public String getInvocation() {
	return invocationArgs;
    }
}
