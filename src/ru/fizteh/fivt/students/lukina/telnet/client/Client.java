package ru.fizteh.fivt.students.lukina.telnet.client;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class Client {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static PrintWriter out = new PrintWriter(System.out, true);
    static ClientParser parser = new ClientParser(in, out);
    static ClientWorker worker = new ClientWorker(out);


    public static void main(String[] args) {

        Thread thread = new Thread(new ShutDownListener());
        Runtime.getRuntime().addShutdownHook(thread);

        if (args.length == 0) {
            parser.interactive(worker);
        } else {
            String wholeArgument = concatStrings(args, " ");
            try {
                parser.packet(worker, wholeArgument);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static String concatStrings(String[] args, String separator) {
        StringBuilder string = new StringBuilder();
        if (args.length == 0) {
            return "";
        }
        string.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null) {
                string.append(separator).append(args[i]);
            }
        }
        return string.toString();
    }

    static class ShutDownListener implements Runnable {
        @Override
        public void run() {
            if (worker.isConnected()) {
                try {
                    worker.disconnect();
                    out.close();
                    in.close();
                } catch (Exception e) {
                    System.out.println("Can't stop the client");
                }
            }
        }
    }

}

