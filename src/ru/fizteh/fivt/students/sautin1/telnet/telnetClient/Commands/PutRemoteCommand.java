package ru.fizteh.fivt.students.sautin1.telnet.telnetClient.Commands;

/**
 * Put command.
 * Created by sautin1 on 10/12/14.
 */
public class PutRemoteCommand extends RemoteDatabaseCommand {

    public PutRemoteCommand() {
        super("put", 2, Integer.MAX_VALUE - 1);
    }

}
