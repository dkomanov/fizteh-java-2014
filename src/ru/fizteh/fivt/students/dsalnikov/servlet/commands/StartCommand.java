package ru.fizteh.fivt.students.dsalnikov.servlet.commands;

import ru.fizteh.fivt.students.dsalnikov.servlet.server.ServletServer;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by Dmitriy on 12/2/2014.
 */
public class StartCommand implements Command {

    ServletServer link;

    public StartCommand(ServletServer server) {
        link = server;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        link.start(new Integer(args[1]));
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
