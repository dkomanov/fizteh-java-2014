package ru.fizteh.fivt.students.irina_karatsapova.proxy.client;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClentMain {
    public static void main(String[] args) {
        System.out.println("Welcome to Client side");

        BufferedReader in  = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out, true);

        Interpreter interpreter = new Interpreter(in, out);
        interpreter.setDefaultCommand(new SendCommand());
        interpreter.addCommand(new ConnectCommand());
        interpreter.addCommand(new DisconnectCommand());
        interpreter.addCommand(new WhereamiCommand());
        interpreter.addCommand(new ExitCommand());

        InterpreterState state = new InterpreterState(out);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (state.connected) {
                    try {
                        state.disconnect();
                        out.close();
                        in.close();
                    } catch (Exception e) {
                        System.err.println("Can't stop the client");
                    }
                }
            }
        });

        interpreter.interactiveMode(state);
    }
}
