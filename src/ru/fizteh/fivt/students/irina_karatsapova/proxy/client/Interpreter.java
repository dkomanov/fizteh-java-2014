package ru.fizteh.fivt.students.irina_karatsapova.proxy.client;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands.Command;

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
    }
    
    public void addCommand(Command command) {
        commander.addCommand(command);
    }

    public void setDefaultCommand(Command command) {
        commander.setDefaultCommand(command);
    }
    
    public void interactiveMode(InterpreterState state) {
        Scanner inScanner = new Scanner(in);
        out.print("$ ");
        out.flush();
        while (inScanner.hasNextLine()) {
            String input;
            input = inScanner.nextLine();
            try {
                batchMode(state, input);
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            out.print("$ ");
            out.flush();
        }
    }
    
    public void batchMode(InterpreterState state, String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String command : commands) {
            commander.startCommand(state, command);
        }
    }
}

