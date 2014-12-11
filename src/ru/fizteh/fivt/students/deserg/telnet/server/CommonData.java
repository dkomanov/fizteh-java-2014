package ru.fizteh.fivt.students.deserg.telnet.server;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by deserg on 11.12.14.
 */
public class CommonData {

    public DbTableProvider db;
    public Set<String> users = new HashSet<>();
    public int port = 10001;


    public CommonData(DbTableProvider db) {

        this.db = db;

    }


    public void setPort(int port) {
        this.port = port;
    }
}
