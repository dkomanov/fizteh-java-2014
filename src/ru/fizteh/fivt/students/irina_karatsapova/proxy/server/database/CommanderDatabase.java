package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ThreadInterruptException;

import java.io.IOException;
import java.util.HashMap;

public class CommanderDatabase {
    
    private HashMap<String, DatabaseCommand> allCommands = new HashMap<>();
    
    public void addCommand(DatabaseCommand command) {
        allCommands.put(command.name(), command);
    }

    public void startCommand(InterpreterStateDatabase state, String commandWithArgs) throws Exception {
        String[] args = commandWithArgs.trim().split("\\s+");
        if (args[0].isEmpty()) {
            return;
        }
        if (!allCommands.containsKey(args[0])) {
            throw new Exception("No such command");
        }
        DatabaseCommand command = allCommands.get(args[0]);
        
        if ((command.minArgs() > args.length) || (command.maxArgs() < args.length)) {
            throw new Exception(command.name() + ": Wrong number of arguments");
        }
        try {
            command.execute(state, args);
        } catch (IOException e) {
            throw new Exception(command.name() + ": Error");
        } catch (DataBaseException e) {
            throw new DataBaseException("DataBase: " + e.getMessage());
        } catch (ThreadInterruptException e) {
            throw new ThreadInterruptException();
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("wrong type (" + e.getMessage() + ")");
        } catch (ColumnFormatException e) {
            throw new Exception("wrong type (" + e.getMessage() + ")");
        } catch (Exception e) {
            throw new Exception(command.name() + ": " + e.getMessage());
        }
    }
}


