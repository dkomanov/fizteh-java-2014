package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.ClientState;

import java.io.InputStream;
import java.io.PrintStream;

public class DisconnectCommand implements Command {

    ClientState state;

    public DisconnectCommand(ClientState state) {
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.disconnect();
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
