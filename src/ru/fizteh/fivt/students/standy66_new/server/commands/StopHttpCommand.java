package ru.fizteh.fivt.students.standy66_new.server.commands;

import java.io.PrintWriter;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class StopHttpCommand extends ServerContextualCommand {
    protected StopHttpCommand(PrintWriter outputWriter, ServerContext context) {
        super(outputWriter, (x -> x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        if (getContext().getHttpServer().isRunning()) {
            getOutputWriter().write(String.format("stopped at port %d%n",
                    getContext().getHttpServer().getAddress().getPort()));
            getOutputWriter().flush();
            getContext().getHttpServer().stop();
        } else {
            getOutputWriter().write("not started");
        }
    }
}
