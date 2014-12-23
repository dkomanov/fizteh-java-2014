package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.Command;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands.CommandListusers;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands.CommandStart;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.TelnetServerCommands.CommandStop;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerShell {
    private static HashMap<String, Command> shellCommands = new HashMap<>();
    private TelnetServer server;

    Scanner scanner;
    AbstractQueue<String> activeCommands;
    PrintStream out;

    static {
        ACommand c = new CommandPut();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandGet();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandRemove();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandExit();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandCreate();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandDrop<>();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandShowTables<>();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandUse();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandSize();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandCommit();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandRollback();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandList();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandStart();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandStop();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandListusers();
        shellCommands.put(c.getCommandName(), c);
    }

    public ServerShell(TelnetServer server, InputStream stream, PrintStream out) {
        this.server = server;
        scanner = new Scanner(stream);
        activeCommands = new ConcurrentLinkedQueue<>();
        this.out = out;
    }

    public String nextCommand() {
        if (activeCommands.isEmpty()) {
            System.out.print("$ ");
            System.out.flush();
            for (String s: scanner.nextLine().split(";\\s*")) {
                activeCommands.add(s);
            }
        }
        return activeCommands.remove();
    }

    public String socketNextCommand() {
        if (activeCommands.isEmpty()) {
            Collections.addAll(activeCommands, scanner.nextLine().split(";\\s*"));
        }
        return activeCommands.remove();
    }

    public boolean execute(final String currentCommand) {
        String commandName = CommandsParser.getCommandName(currentCommand);
        String parameters = CommandsParser.getCommandParameters(currentCommand);
        if (commandName.isEmpty()) {
            out.println(this.getClass().toString() + ": Empty command name!");
            return false;
        }
        if (!shellCommands.containsKey(commandName)) {
            out.println(this.getClass().toString() + ": Unknown command: \'" + commandName + "\'");
            return false;
        }
        boolean result = true;
        try {
            shellCommands.get(commandName)
                    .executeCommand(parameters, server, out);
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
            result = false;
        } catch (Exception e) {
            out.println(e.getMessage());
            result = false;
        }
        return result;
    }
}

