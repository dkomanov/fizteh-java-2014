package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Command for deleting database table.
 * Created by sautin1 on 10/20/14.
 */
public class DropRemoteCommand extends RemoteDatabaseCommand {

    public DropRemoteCommand() {
        super("drop", 1, 1);
    }
}
