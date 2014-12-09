package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.ClientState;

import java.io.InputStream;
import java.io.PrintStream;

public class ConnectCommand implements Command {

    ClientState state;

    public ConnectCommand(ClientState state) {
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.connect(args[1], Integer.parseInt(args[2]));
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
