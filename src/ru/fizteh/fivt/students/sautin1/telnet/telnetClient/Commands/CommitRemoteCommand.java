package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Commit command.
 * Created by sautin1 on 10/12/14.
 */
public class CommitRemoteCommand extends RemoteDatabaseCommand {

    public CommitRemoteCommand() {
        super("commit", 0, 0);
    }
}
