package ru.fizteh.fivt.students.lukina.telnet.client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;


public class ClientParser {
    BufferedReader in;
    PrintWriter out;

    public ClientParser(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    private static void printError(String strError) {
        System.out.println(strError);
    }

    private static boolean checkArgs(int num, String[] args) {
        return args.length == num;
    }


    public void interactive(ClientWorker worker) {
        Scanner inScanner = new Scanner(in);
        out.print("$ ");
        out.flush();
        while (inScanner.hasNextLine()) {
            String input;
            input = inScanner.nextLine();
            try {
                packet(worker, input);
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            out.print("$ ");
            out.flush();
        }
    }

    public void packet(ClientWorker worker, String arg) {
        String[] commands = arg.split(";");
        for (String commandWithArgs : commands) {
            String[] args = commandWithArgs.trim().split("\\s+");
            if (args[0].isEmpty()) {
                return;
            }
            switch (args[0]) {
                case "connect":
                    if (checkArgs(3, args)) {
                        try {
                            worker.connect(args);
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "disconnect":
                    if (checkArgs(1, args)) {
                        try {
                            worker.disconnect();
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "exit":
                    if (checkArgs(1, args)) {
                        try {
                            worker.exit();
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "whereami":
                    if (checkArgs(1, args)) {
                        try {
                            worker.whereAmI();
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                default:
                    try {
                        worker.send(args);
                    } catch (Exception e) {
                        printError(e.getMessage());
                    }
            }
        }
    }

}
