package ru.fizteh.fivt.students.standy66_new.server.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.fizteh.fivt.students.standy66_new.server.DbServer;
import ru.fizteh.fivt.students.standy66_new.server.http.servlets.*;

import java.net.InetSocketAddress;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class HttpDbServer implements DbServer {
    private Server server;

    public HttpDbServer(InetSocketAddress inetSocketAddress) {
        if (inetSocketAddress == null) {
            throw new IllegalArgumentException("inetSocketAddress should not be null");
        }
        server = new Server(inetSocketAddress);
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new BeginServlet()), "/begin");
        servletContextHandler.addServlet(new ServletHolder(new CommitServlet()), "/commit");
        servletContextHandler.addServlet(new ServletHolder(new RollbackServlet()), "/rollback");
        servletContextHandler.addServlet(new ServletHolder(new GetServlet()), "/get");
        servletContextHandler.addServlet(new ServletHolder(new PutServlet()), "/put");
        servletContextHandler.addServlet(new ServletHolder(new SizeServlet()), "/size");
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
