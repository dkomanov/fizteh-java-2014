package ru.fizteh.fivt.students.dsalnikov.telnet.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.ServerState;

import java.io.InputStream;
import java.io.PrintStream;

public class ListUsersCommand extends AbstractCommand {

    private ServerState link;

    public ListUsersCommand(ServerState link) {
        super("listusers", 0);
        this.link = link;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        link.listUsers().forEach(System.out::println);
    }
}
