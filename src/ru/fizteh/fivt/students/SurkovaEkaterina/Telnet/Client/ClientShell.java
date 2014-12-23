package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands.CommandClientExit;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands.CommandConnect;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands.CommandDisconnect;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client.TelnetClientCommands.CommandWhereami;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.FileMapCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.Command;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.util.HashMap;
import java.util.Scanner;

public class ClientShell {
    private static HashMap<String, Command> shellCommands = new HashMap<>();
    private TelnetClient client;
    String[] arguments;
    private static String invitation = "$ ";

    ClientShell(TelnetClient client) {
        this.client = client;
    }

    static {
        ACommand c = new CommandConnect();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandDisconnect();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandWhereami();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandPut();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandGet();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandRemove();
        shellCommands.put(c.getCommandName(), c);
        c = new CommandClientExit<>();
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
    }

    public final void setArguments(final String[] args) {
        this.arguments = args;
    }

    public final void beginExecuting() {
        if ((arguments == null) || (arguments.length == 0)) {
            interactiveMode();
        } else {
            packageMode();
        }
    }

    private void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(invitation);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String[] commands = CommandsParser.parseCommands(command);
            if (command.length() == 0) {
                System.out.print(invitation);
                continue;
            }
            for (String currentCommand:commands) {
                if (!execute(currentCommand)) {
                    break;
                }
            }
            System.out.print(invitation);
        }
        scanner.close();
    }

    private void packageMode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s:arguments) {
            stringBuilder.append(s + " ");
        }
        String[] commands = CommandsParser.parseCommands(
                stringBuilder.toString());
        for (String currentCommand:commands) {
            boolean result = execute(currentCommand);
            if (!result) {
                System.exit(-1);
            }
        }

    }

    public boolean execute(final String currentCommand) {
        String commandName = CommandsParser.getCommandName(currentCommand);
        if (client.state == ClientState.NOT_CONNECTED
                && !commandName.equals("connect")
                && !commandName.equals("exit")) {
            System.out.println("Connection is not established yet!");
            return false;
        }
        String parameters = CommandsParser.getCommandParameters(currentCommand);
        if (commandName.isEmpty()) {
            System.out.println(this.getClass().toString() + ": Empty command name!");
            return false;
        }
        if (!shellCommands.containsKey(commandName)) {
            System.out.println(this.getClass().toString() + ": Unknown command: \'" + commandName + "\'");
            return false;
        }
        boolean result = true;
        try {
            shellCommands.get(commandName)
                    .executeCommand(parameters, client, System.out);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            result = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
}
