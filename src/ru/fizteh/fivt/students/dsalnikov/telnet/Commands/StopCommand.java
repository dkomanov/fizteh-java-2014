package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.ServerState;

import java.io.InputStream;
import java.io.PrintStream;

public class StopCommand extends AbstractCommand {

    ServerState state;

    public StopCommand(ServerState state) {
        super("stop", 0);
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.stop();
    }
}
