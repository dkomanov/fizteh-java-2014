package ru.fizteh.fivt.students.irina_karatsapova.junit;

import ru.fizteh.fivt.students.irina_karatsapova.junit.commands.Command;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;

import java.io.IOException;
import java.util.HashMap;

public class Commander {
    
    private HashMap<String, Command> allCommands = new HashMap<String, Command>();
    
    public void addCommand(Command command) {
        allCommands.put(command.name(), command);
    }
    
    public void startCommand(TableProvider tableProvider, String commandWithArgs) throws Exception {
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
            command.execute(tableProvider, args);
        } catch (IOException e) {
            throw new Exception(command.name() + ": Error");
        } catch (DataBaseException e) {
            throw new DataBaseException("DataBase: " + e.getMessage());
        } catch (TableException e) {
            throw new DataBaseException("Table: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(command.name() + ": " + e.getMessage());
        }
    }
}


