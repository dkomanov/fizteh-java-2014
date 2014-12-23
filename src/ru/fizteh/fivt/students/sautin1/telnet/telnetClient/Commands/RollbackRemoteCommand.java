package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Rollback command.
 * Created by sautin1 on 10/12/14.
 */
public class RollbackRemoteCommand extends RemoteDatabaseCommand {

    public RollbackRemoteCommand() {
        super("rollback", 0, 0);
    }
}
