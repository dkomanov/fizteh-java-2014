package ru.fizteh.fivt.students.standy66_new.server.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.standy66_new.server.DbServer;
import ru.fizteh.fivt.students.standy66_new.server.http.servlets.*;
import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.server.tdb.TransactionDbImpl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class HttpDbServer implements DbServer {
    private Server server;
    private ServletContextHandler servletContextHandler;
    private TransactionDbImpl db;
    private Map<Integer, Transaction> transactionMap = new ConcurrentHashMap<>();
    private boolean running = false;
    private InetSocketAddress address;

    public HttpDbServer(File dbDirectory) throws IOException, ParseException {
        if (dbDirectory == null) {
            throw new IllegalArgumentException("dbDirectory should not be null");
        }
        db = new TransactionDbImpl(dbDirectory);
        DbBinder binder = new DbBinder() {
            @Override
            public int beginTransaction(String tableName) {
                Transaction transaction = db.beginTransaction(tableName);
                if (transaction != null) {
                    Random random = new Random(System.currentTimeMillis());
                    int randomInt = 1;
                    while (randomInt == 0 || transactionMap.containsKey(randomInt)) {
                        randomInt = random.nextInt(100000);
                    }
                    transactionMap.put(randomInt, transaction);
                    return randomInt;
                } else {
                    return 0;
                }
            }

            @Override
            public Transaction getTransaction(int id) {
                return transactionMap.get(id);
            }
        };
        servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new BeginServlet(binder)), "/begin");
        servletContextHandler.addServlet(new ServletHolder(new CommitServlet(binder)), "/commit");
        servletContextHandler.addServlet(new ServletHolder(new RollbackServlet(binder)), "/rollback");
        servletContextHandler.addServlet(new ServletHolder(new GetServlet(binder)), "/get");
        servletContextHandler.addServlet(new ServletHolder(new PutServlet(binder)), "/put");
        servletContextHandler.addServlet(new ServletHolder(new SizeServlet(binder)), "/size");
        servletContextHandler.setContextPath("/");
    }

    @Override
    public void start(InetSocketAddress address) throws Exception {
        if (address == null) {
            throw new IllegalArgumentException("address should not be null");
        }
        if (running) {
            throw new IllegalStateException("server is already running");
        }
        this.address = address;
        running = true;
        server = new Server(address);
        server.setHandler(servletContextHandler);
        server.start();
    }

    @Override
    public void stop() throws Exception {
        running = false;
        this.address = null;
        server.stop();
    }

    public boolean isRunning() {
        return running;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
