package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.ServerState;

import java.io.InputStream;
import java.io.PrintStream;

public class ListUsersCommand implements Command {

    private ServerState link;

    public ListUsersCommand(ServerState link) {
        this.link = link;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        link.listUsers().forEach(System.out::println);
    }

    @Override
    public String getName() {
        return "listusers";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
