package ru.fizteh.fivt.students.deserg.telnet.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by deserg on 11.12.14.
 */
public class CommonData {

    private DbTableProvider db;
    private Set<String> users = new HashSet<>();
    private int port = 10001;
    private boolean started = false;
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public CommonData(DbTableProvider db) {

        this.db = db;

    }


    public void setPort(int port) {
        lock.writeLock().lock();
        this.port = port;
        lock.writeLock().unlock();
    }

    public int getPort() {
        return port;
    }

    public void setDb(DbTableProvider db) {
        lock.writeLock().lock();
        this.db = db;
        lock.writeLock().unlock();
    }

    public DbTableProvider getDb() {
        return db;
    }


    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        lock.writeLock().lock();
        this.users = users;
        lock.writeLock().unlock();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        lock.writeLock().lock();
        this.started = started;
        lock.writeLock().unlock();
    }
}
