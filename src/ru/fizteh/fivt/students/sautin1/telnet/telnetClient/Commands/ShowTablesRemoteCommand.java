package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Command for showing all tables in database.
 * Created by sautin1 on 10/20/14.
 */
public class ShowTablesRemoteCommand extends RemoteDatabaseCommand {

    public ShowTablesRemoteCommand() {
        super("show", 1, 1);
    }
}
