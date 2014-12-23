package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.ExitCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.database_commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.table_commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ThreadInterruptException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class InterpreterDatabase {
    private CommanderDatabase commander = new CommanderDatabase();
    BufferedReader in;
    PrintWriter out;
    
    public InterpreterDatabase(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
        addCommand(new PutCommand());
        addCommand(new GetCommand());
        addCommand(new RemoveCommand());
        addCommand(new ExitCommand());
        addCommand(new CreateCommand());
        addCommand(new DropCommand());
        addCommand(new UseCommand());
        addCommand(new ShowCommand());
        addCommand(new SizeCommand());
        addCommand(new CommitCommand());
        addCommand(new RollbackCommand());
        addCommand(new ListCommand());
    }
    
    public void addCommand(DatabaseCommand command) {
        commander.addCommand(command);
    }
    
    public void interactiveMode(InterpreterStateDatabase state) {
        BufferedReader inReader = new BufferedReader(in);
        while (true) {
            String input;
            try {
                while (!inReader.ready()) {
                    if (Thread.currentThread().isInterrupted()) {
                        inReader.close();
                        return;
                    }
                }
                input = inReader.readLine();
            } catch (IOException e) {
                return;
            }
            try {
                batchMode(state, input);
            } catch (DataBaseException e) {
                out.println(e.getMessage());
                System.exit(1);
            } catch (ThreadInterruptException e) {
                return;
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
    }
    
    public void batchMode(InterpreterStateDatabase state, String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String command : commands) {
            commander.startCommand(state, command);
        }
    }
}

