package ru.fizteh.fivt.students.standy66_new.server;

import java.net.InetSocketAddress;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public interface DbServer {
    void start(InetSocketAddress address) throws Exception;
    void stop() throws Exception;

    boolean isRunning();

    InetSocketAddress getAddress();
}
