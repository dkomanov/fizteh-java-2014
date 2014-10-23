package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.Command;
import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.support.Utility;

public class WrongArgsNumberException extends IllegalArgumentException {

    private static final long serialVersionUID = 3047879410299126653L;

    private static String makeMessage(Command command) {
	StringBuilder sb = new StringBuilder("Wrong arguments number; invocation: ");
	sb.append(Utility.simplifyClassName(command.getClass().getSimpleName()));
	if (command.getInvocation() != null) {
	    sb.append(" ").append(command.getInvocation());
	}
	return sb.toString();
    }

    public WrongArgsNumberException(Command command) {
	super(makeMessage(command));
    }
}
