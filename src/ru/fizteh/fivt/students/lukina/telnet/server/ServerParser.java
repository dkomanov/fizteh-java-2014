package ru.fizteh.fivt.students.lukina.telnet.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;


public class ServerParser {
    BufferedReader in;
    PrintWriter out;

    public ServerParser(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    private static void printError(String strError) {
        System.out.println(strError);
    }

    private static boolean checkArgs(int num, String[] args) {
        return args.length == num;
    }

    public void interactive(ServerWorker worker) {
        Scanner inScanner = new Scanner(in);
        while (true) {
            out.print("$ ");
            out.flush();
            String input;
            if (inScanner.hasNextLine()) {
                input = inScanner.nextLine();
            } else {
                break;
            }
            try {
                packet(worker, input);
            } catch (Exception e) {
                out.println(e.getMessage());
                return;
            }
        }
    }

    public void packet(ServerWorker worker, String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String commandWithArgs : commands) {
            String[] args = commandWithArgs.trim().split("\\s+");
            if (args[0].isEmpty()) {
                return;
            }
            switch (args[0]) {

                case "address":
                    out.println(worker.getAddr());
                    break;
                case "start":
                    if (checkArgs(1, args) || checkArgs(2, args)) {
                        try {
                            worker.start(args);
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "stop":
                    if (checkArgs(1, args)) {
                        try {
                            worker.stop();
                        } catch (Exception e) {
                            printError(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "listusers":
                    if (checkArgs(1, args)) {
                        try {
                            worker.listUsers();
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
                default:
                    printError("unknown command format");
            }
        }
    }
}

