package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.ClientState;

import java.io.InputStream;
import java.io.PrintStream;

public class WhereAmICommand implements Command {

    ClientState state;

    public WhereAmICommand(ClientState state) {
        this.state = state;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        outputStream.println(state.whereAmI());
    }

    @Override
    public String getName() {
        return "whereami";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
