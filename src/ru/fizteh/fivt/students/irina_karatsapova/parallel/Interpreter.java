package ru.fizteh.fivt.students.irina_karatsapova.parallel;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.commands.*;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.ThreadInterruptException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Interpreter {
    private Commander commander = new Commander();
    BufferedReader in;
    PrintWriter out;
    
    public Interpreter(BufferedReader in, PrintWriter out) {
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
    
    public void addCommand(Command command) {
        commander.addCommand(command);
    }
    
    public void interactiveMode(InterpreterState state) {
        Scanner inScanner = new Scanner(in);
        out.println("$ ");
        while (inScanner.hasNextLine()) {
            String input;
            input = inScanner.nextLine();
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
            out.println("$ ");
        }
    }
    
    public void batchMode(InterpreterState state, String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String command : commands) {
            commander.startCommand(state, command);
        }
    }
}

