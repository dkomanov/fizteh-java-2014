package ru.fizteh.fivt.students.sautin1.shell;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sautin1 on 9/30/14.
 */
public class Shell {
    private final Map<String, Command> commandsMap = new HashMap<String, Command>();

    {
        Command command;
        command = new CommandCat();
        commandsMap.put(command.toString(), command);
        command = new CommandCd();
        commandsMap.put(command.toString(), command);
        command = new CommandCp();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandMkDir();
        commandsMap.put(command.toString(), command);
        command = new CommandMv();
        commandsMap.put(command.toString(), command);
        command = new CommandPwd();
        commandsMap.put(command.toString(), command);
        command = new CommandRm();
        commandsMap.put(command.toString(), command);
    }

    public void executeCommand(String... commandWithParams) {
        Command command = commandsMap.get(commandWithParams[0]);
        if (command == null) {
            throw new IllegalArgumentException(commandWithParams[0] + ": command not found");
        }
        if (command instanceof CommandExit) {
            return;
        }
        try {
            command.execute(commandWithParams);
        } catch (IllegalArgumentException e) {

        }
    }

    public void interactWithUser() {

    }
}
