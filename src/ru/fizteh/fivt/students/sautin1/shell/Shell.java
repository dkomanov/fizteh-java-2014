package ru.fizteh.fivt.students.sautin1.shell;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sautin1 on 9/30/14.
 */
public class Shell {
    private final Map<String, Command> commandsMap = new HashMap<String, Command>();

    {
        Command command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
    }

    public void interactWithUser() {

    }

    public void executeCommand(String... commandWithParams) {

    }
}
