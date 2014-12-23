package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Command for setting a definite database table as active.
 * Created by sautin1 on 10/20/14.
 */
public class UseRemoteCommand extends RemoteDatabaseCommand {

    public UseRemoteCommand() {
        super("use", 1, 1);
    }
}
