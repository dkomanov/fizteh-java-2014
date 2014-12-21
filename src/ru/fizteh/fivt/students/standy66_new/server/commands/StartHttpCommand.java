package ru.fizteh.fivt.students.standy66_new.server.commands;

import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class StartHttpCommand extends ServerContextualCommand {
    protected StartHttpCommand(PrintWriter outputWriter, ServerContext context) {
        super(outputWriter, (x -> x == 2 || x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        int port = 10001;
        if (arguments.length == 2) {
            port = Integer.parseInt(arguments[1]);
        }
        if (getContext().getHttpServer().isRunning()) {
            System.err.println("not started: already started");
        } else if (port <= 0 || port >= 65536) {
            System.err.println("not started: invalid port number");
        } else {
            getContext().getHttpServer().start(new InetSocketAddress(port));
            System.err.println("started at " + port);

        }
    }
}
