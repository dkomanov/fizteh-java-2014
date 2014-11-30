package ru.fizteh.fivt.students.standy66_new.server.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.standy66_new.Interpreter;
import ru.fizteh.fivt.students.standy66_new.server.DbServer;
import ru.fizteh.fivt.students.standy66_new.server.http.servlets.*;
import ru.fizteh.fivt.students.standy66_new.server.tdb.Transaction;
import ru.fizteh.fivt.students.standy66_new.server.tdb.TransactionDb;
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
    private TransactionDbImpl db;
    private Map<Integer, Transaction> transactionMap = new ConcurrentHashMap<>();

    public HttpDbServer(InetSocketAddress inetSocketAddress, File dbDirectory) throws IOException, ParseException {
        if (inetSocketAddress == null) {
            throw new IllegalArgumentException("inetSocketAddress should not be null");
        }
        server = new Server(inetSocketAddress);
        db = new TransactionDbImpl(dbDirectory);
        DbBinder binder = new DbBinder() {
            @Override
            public TransactionDb getDb() {
                return db;
            }

            @Override
            public int putTransaction(Transaction transaction) {
                Random random = new Random(System.currentTimeMillis());
                int randomInt = 0;
                while (transactionMap.containsKey(randomInt = random.nextInt(100000)));
                transactionMap.put(randomInt, transaction);
                return randomInt;
            }

            @Override
            public Transaction getTransaction(int id) {
                return transactionMap.get(id);
            }
        };
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new BeginServlet(binder)), "/begin");
        servletContextHandler.addServlet(new ServletHolder(new CommitServlet(binder)), "/commit");
        servletContextHandler.addServlet(new ServletHolder(new RollbackServlet(binder)), "/rollback");
        servletContextHandler.addServlet(new ServletHolder(new GetServlet(binder)), "/get");
        servletContextHandler.addServlet(new ServletHolder(new PutServlet(binder)), "/put");
        servletContextHandler.addServlet(new ServletHolder(new SizeServlet(binder)), "/size");
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
    }

    @Override
    public void start() throws Exception {
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }
}
