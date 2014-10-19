package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.HandledException;

/**
 * This class represents an executable shell command
 * 
 * @author phoenix
 * 
 */
public interface Command {
    public void execute(Shell shell, String[] args) throws HandledException;

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