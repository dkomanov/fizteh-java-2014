package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.ClientState;

import java.io.InputStream;
import java.io.PrintStream;

public class ConnectCommand extends AbstractCommand {

    ClientState state;

    public ConnectCommand(ClientState state) {
        super("connect", 2);
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.connect(args[1], Integer.parseInt(args[2]));
    }
}

