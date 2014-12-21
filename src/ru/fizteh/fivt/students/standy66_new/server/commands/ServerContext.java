package ru.fizteh.fivt.students.standy66_new.server.commands;

import ru.fizteh.fivt.students.standy66_new.server.DbServer;

import java.net.InetSocketAddress;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
class ServerContext {
    private DbServer httpServer;
    private DbServer telnetServer;

    ServerContext(DbServer httpServer, DbServer telnetServer) {
        //TODO: remove in production
        telnetServer = new DbServer() {
            @Override
            public void start(InetSocketAddress address) throws Exception {

            }

            @Override
            public void stop() throws Exception {

            }

            @Override
            public InetSocketAddress getAddress() {
                return null;
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
        if (httpServer == null || telnetServer == null) {
            throw new IllegalArgumentException("httpServer or telnetServer is null");
        }
        this.httpServer = httpServer;
        this.telnetServer = telnetServer;
    }

    DbServer getHttpServer() {
        return httpServer;
    }

    DbServer getTelnetServer() {
        return telnetServer;
    }
}
