package ru.fizteh.fivt.students.standy66_new.server.commands;

import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.commands.CommandFactory;
import ru.fizteh.fivt.students.standy66_new.server.DbServer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class ServerCommandFactory implements CommandFactory {
    private ServerContext context;
    private PrintWriter writer;

    public ServerCommandFactory(PrintWriter writer, DbServer httpServer, DbServer telnetServer) {
        this.context = new ServerContext(httpServer, telnetServer);
        this.writer = writer;
    }

    public Map<String, Command> getCommandMap(String locale) {
        return new HashMap<String, Command>() {
            {
                put("starthttp", getStartHttpCommand());
                put("stophttp", getStopHttpCommand());
                put("starttelnet", getStartTelnetCommand());
                put("stoptelnet", getStopTelnetCommand());
            }
        };
    }

    private Command getStartHttpCommand() {
        return new StartHttpCommand(writer, context);
    }

    private Command getStopHttpCommand() {
        return new StopHttpCommand(writer, context);
    }

    private Command getStartTelnetCommand() {
        return new StartTelnetCommand(writer, context);
    }

    private Command getStopTelnetCommand() {
        return new StopTelnetCommand(writer, context);
    }
}
