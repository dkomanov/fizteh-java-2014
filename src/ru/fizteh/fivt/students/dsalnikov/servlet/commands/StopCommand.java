package ru.fizteh.fivt.students.dsalnikov.servlet.commands;

import ru.fizteh.fivt.students.dsalnikov.servlet.server.ServletServer;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by Dmitriy on 12/2/2014.
 */
public class StopCommand implements Command {

    ServletServer server;

    public StopCommand(ServletServer server) {
        this.server = server;
    }


    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        server.stop();
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
