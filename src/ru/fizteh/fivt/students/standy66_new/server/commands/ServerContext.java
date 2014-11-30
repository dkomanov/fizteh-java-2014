package ru.fizteh.fivt.students.standy66_new.server.commands;

import ru.fizteh.fivt.students.standy66_new.server.DbServer;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class ServerContext {
    private DbServer httpServer;
    private DbServer telnetServer;

    public ServerContext(DbServer httpServer, DbServer telnetServer) {
        this.httpServer = httpServer;
        this.telnetServer = telnetServer;
    }

    public DbServer getHttpServer() {
        return httpServer;
    }

    public DbServer getTelnetServer() {
        return telnetServer;
    }
}
