package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Command for creating database table.
 * Created by sautin1 on 10/20/14.
 */
public class CreateRemoteCommand extends RemoteDatabaseCommand {

    public CreateRemoteCommand() {
        super("create", 2, Integer.MAX_VALUE - 1);
    }
}
