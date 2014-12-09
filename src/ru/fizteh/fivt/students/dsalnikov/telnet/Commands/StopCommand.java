package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.ServerState;

import java.io.InputStream;
import java.io.PrintStream;

public class StopCommand implements Command {

    ServerState state;

    public StopCommand(ServerState state) {
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        state.stop();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
