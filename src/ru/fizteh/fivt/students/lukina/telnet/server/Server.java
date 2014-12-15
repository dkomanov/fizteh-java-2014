package ru.fizteh.fivt.students.lukina.telnet.server;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.lukina.proxy.DBaseProviderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {
    public static String mainDir = "fizteh.db.dir";
    static TableProviderFactory tableProviderFactory = new DBaseProviderFactory();
    static TableProvider tableProvider;
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static PrintWriter out = new PrintWriter(System.out, true);

    static ServerParser parser = new ServerParser(in, out);
    static ServerWorker worker;

    public static void main(String[] args) {
        System.setProperty(mainDir, "/tmp/newdb");
        if (System.getProperty(Server.mainDir) == null) {
            System.err.println("Path to the database is not set up. Use -D" + Server.mainDir + "=...");
            System.exit(1);
        }
        try {
            TableProvider tableProvider = tableProviderFactory.create(System.getProperty(Server.mainDir));
            worker = new ServerWorker(tableProvider, out);
            System.out.println();
            Thread thread = new Thread(new ShutDownListener());
            Runtime.getRuntime().addShutdownHook(thread);
            parser.interactive(worker);
        } catch (IOException e) {
            System.out.println("couldn't create table");
        }
    }

    static class ShutDownListener implements Runnable {
        @Override
        public void run() {
            if (worker.isStarted()) {
                try {
                    worker.stop();
                    out.close();
                    in.close();
                } catch (Exception e) {
                    System.err.println("Can't stop the server");
                }
            }
        }
    }
}
