package ru.fizteh.fivt.students.dsalnikov.servlet.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.dsalnikov.servlet.database.TransactionManager;
import ru.fizteh.fivt.students.dsalnikov.servlet.server.servlets.*;

import java.io.IOException;

public class ServletServer {
    private static final int DEFAULT_PORT_NUMBER = 8080;
    private Server server;
    private TransactionManager manager;

    public ServletServer(TransactionManager manager) {
        this.manager = manager;
    }

    public void start(int port) throws Exception {
        if (server != null && server.isStarted()) {
            throw new IllegalStateException("Server already started");
        }

        if (port == -1) {
            port = DEFAULT_PORT_NUMBER;
        }

        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new BeginServlet(manager)), Paths.BEGIN);
        context.addServlet(new ServletHolder(new CommitServlet(manager)), Paths.COMMIT);
        context.addServlet(new ServletHolder(new GetServlet(manager)), Paths.GET);
        context.addServlet(new ServletHolder(new PutServlet(manager)), Paths.PUT);
        context.addServlet(new ServletHolder(new RollbackServlet(manager)), Paths.ROLLBACK);
        context.addServlet(new ServletHolder(new SizeServlet(manager)), Paths.SIZE);

        server.setHandler(context);

        server.start();

    }

    public int getPort() {
        return server.getConnectors()[0].getPort();
    }

    public void stop() throws IOException {
        if (server == null || !server.isStarted()) {
            throw new IllegalStateException("Server isn't started");
        }
        try {
            server.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
