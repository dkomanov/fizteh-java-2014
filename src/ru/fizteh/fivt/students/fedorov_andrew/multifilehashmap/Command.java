package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.TerminalException;

/**
 * This class represents an executable shell command
 * 
 * @author phoenix
 * 
 */
public interface Command {
    void execute(Shell shell, String[] args) throws TerminalException;

    /**
     * Information text for the command
     * 
     * @return
     */
    String getInfo();

    /**
     * Complete formula for command invocation excluding command name.
     * 
     * @return
     */
    String getInvocation();
}
