package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.ServerState;

import java.io.InputStream;
import java.io.PrintStream;

public class StartCommand extends AbstractCommand {
    private final int defaultPort = 10001;

    private ServerState state;

    public StartCommand(ServerState state) {
        super("start", 1);
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        int port = Integer.parseInt(args[1]);
        state.start(port);
        System.out.println(String.format("server started on port %s", port));
    }
}
