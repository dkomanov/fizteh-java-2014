package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands.Command;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.TableException;

import java.io.IOException;
import java.util.HashMap;

public class Commander {
    
    private HashMap<String, Command> allCommands = new HashMap<String, Command>();
    
    public void addCommand(Command command) {
        allCommands.put(command.name(), command);
    }
    
    public void startCommand(String commandWithArgs) throws Exception {
        String[] args = commandWithArgs.trim().split("\\s+");
        if (args[0].equals("")) {
            return;
        }
        if (!allCommands.containsKey(args[0])) {
            throw new Exception("No such command");
        }
        Command command = allCommands.get(args[0]);
        
        if ((command.minArgs() > args.length) || (command.maxArgs() < args.length)) {
            throw new Exception(command.name() + ": Wrong number of arguments");
        }
        try {
            command.execute(args);
        } catch (IOException e) {
            throw new Exception(command.name() + ": Error");
        } catch (TableException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception(command.name() + ": " + e.getMessage());
        }
    }
}


