package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.ClientState;

import java.io.InputStream;
import java.io.PrintStream;

public class DisconnectCommand extends AbstractCommand {

    ClientState state;

    public DisconnectCommand(ClientState state) {
        super("disconnect", 0);
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.disconnect();
    }
}
