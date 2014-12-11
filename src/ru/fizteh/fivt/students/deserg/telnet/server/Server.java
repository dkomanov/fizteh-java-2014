package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.deserg.telnet.Program;
import ru.fizteh.fivt.students.deserg.telnet.server.commands.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Created by deserg on 11.12.14.
 */
public class Server implements Program {

    private Map<String, DbCommand> commandMap = new HashMap<>();
    private Queue<ArrayList<String>> argumentsQueue = new LinkedList<>();
    private TableProvider db;
    private int port = 10001;
    ServerSocket socket;

    public Server() {

        String dbDir = System.getProperty("fizteh.db.dir");


        DbTableProviderFactory factory = new DbTableProviderFactory();
        db = factory.create(dbDir);

        commandMap.put("exit", new TableExit());
        commandMap.put("get", new TableGet());
        commandMap.put("list", new TableList());
        commandMap.put("put", new TablePut());
        commandMap.put("remove", new TableRemove());
        commandMap.put("size", new TableSize());
        commandMap.put("commit", new TableCommit());
        commandMap.put("rollback", new TableRollback());
        commandMap.put("create", new DbCreate());
        commandMap.put("drop", new DbDrop());
        commandMap.put("use", new DbUse());
        commandMap.put("show", new DbShowTables());

    }

    @Override
    public void work(String[] args) {



    }

    public void acceptConnections() {

        try {
            socket = new ServerSocket(port);



        } catch (IOException ex) {
            System.out.println("Error with socket");
            System.exit(1);
        }

    }

}
