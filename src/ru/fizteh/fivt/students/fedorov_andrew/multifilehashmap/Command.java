package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.TerminalException;

/**
 * This class represents an executable shell command
 * 
 * @author phoenix
 * 
 */
public interface Command {
    public void execute(Shell shell, String[] args) throws TerminalException;

    /**
     * Information text for the command
     * 
     * @return
     */
    public String getInfo();

    /**
     * Complete formula for command invocation excluding command name.
     * 
     * @return
     */
    public String getInvocation();
}
