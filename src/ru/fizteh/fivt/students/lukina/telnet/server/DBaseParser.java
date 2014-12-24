package ru.fizteh.fivt.students.lukina.telnet.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class DBaseParser {
    BufferedReader in;
    PrintWriter out;

    public DBaseParser(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void interactive(DBaseWorker state) {
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
                packet(state, input);
            } catch (Exception e) {
                out.println(e.getMessage());
                return;
            }
        }
    }

    private void printError(String error) {
        out.println(error);
    }

    private boolean checkArgs(int num, String[] args) {
        return num == args.length;
    }

    public void packet(DBaseWorker worker, String arg) {
        String[] commands = arg.split(";");
        for (String commandWithArgs : commands) {
            String[] args = commandWithArgs.trim().split("\\s+");
            if (args[0].isEmpty()) {
                return;
            }
            switch (args[0]) {
                case "commit":
                    if (checkArgs(1, args)) {
                        try {
                            worker.commit();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "create":
                    if (!checkArgs(0, args) && !checkArgs(1, args) && !checkArgs(2, args)) {
                        try {
                            worker.create(args);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "drop":
                    if (checkArgs(2, args)) {
                        try {
                            worker.drop(args);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "rollback":
                    if (checkArgs(1, args)) {
                        try {
                            worker.rollback();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "show":
                    if (checkArgs(2, args)) {
                        try {
                            worker.show(args);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "use":
                    if (checkArgs(2, args)) {
                        try {
                            worker.use(args);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "get":
                    if (checkArgs(2, args)) {
                        worker.get(args);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "list":
                    if (checkArgs(1, args)) {
                        try {
                            worker.list();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "put":
                    if (!checkArgs(0, args) && !checkArgs(1, args) && !checkArgs(2, args)) {
                        try {
                            worker.put(args);
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "remove":
                    if (checkArgs(2, args)) {
                        worker.remove(args);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "size":
                    if (checkArgs(1, args)) {
                        try {
                            worker.size();
                        } catch (Exception e) {
                            out.println(e.getMessage());
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

