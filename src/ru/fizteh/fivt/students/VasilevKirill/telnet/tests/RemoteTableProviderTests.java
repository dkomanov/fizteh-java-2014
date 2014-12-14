package ru.fizteh.fivt.students.VasilevKirill.telnet.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyRemoteTableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Kirill on 14.12.2014.
 */
public class RemoteTableProviderTests {
    private static RemoteTableProvider remoteTableProvider;
    private static TableProvider tableProvider;
    private static ServerSocket serverSocket;
    private static Socket listenSocket;
    private static Socket clientSocket;
    private static Thread server;
    private static Thread client;
    private static final Object MONITOR = new Object();
    private static DataInputStream serverIn;
    private static DataOutputStream serverOut;

    @BeforeClass
    public static void beforeClass() {
        try {
            serverSocket = new ServerSocket(10001);
            clientSocket = new Socket("127.0.0.1", 10001);
            listenSocket = serverSocket.accept();
            remoteTableProvider = new MyRemoteTableProvider(clientSocket);
            tableProvider = new MyTableProvider(Files.createTempDirectory("database1").toString());
            serverIn = new DataInputStream(listenSocket.getInputStream());
            serverOut = new DataOutputStream(listenSocket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void afterClass() {
        try {
            clientSocket.close();
            listenSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void createAndRemoveTest() {
        try {
            server = new Thread(() -> {
                try {
                    String firstCommand = serverIn.readUTF();
                    String[] args = firstCommand.split("\\s+");
                    String name = args[1];
                    List<Class<?>> typeList = new ArrayList<>();
                    for (int i = 2; i < args.length; ++i) {
                        typeList.add(Class.forName("java.lang." + args[i]));
                    }
                    tableProvider.createTable(name, typeList);
                    serverOut.writeInt(0);
                    assertNotNull(tableProvider.getTable("First")); //Checking that table "First" exists
                    String removeCommand = serverIn.readUTF();
                    args = removeCommand.split("\\s+");
                    name = args[1];
                    serverOut.writeInt(0);
                    tableProvider.removeTable(name);
                    assertNull(tableProvider.getTable("First")); //Checking that table "First" was deleted
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            client = new Thread(() -> {
                Class[] typeList = {Integer.class, String.class};
                try {
                    remoteTableProvider.createTable("First", Arrays.asList(typeList));
                    remoteTableProvider.removeTable("First");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            client.start();
            server.start();
            client.join();
            server.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
    }

    @Test
    public void showTablesTest() {
        try {
            server = new Thread(() -> {
                try {
                    String firstCommand = serverIn.readUTF();
                    String[] args = firstCommand.split("\\s+");
                    String name = args[1];
                    List<Class<?>> typeList = new ArrayList<>();
                    for (int i = 2; i < args.length; ++i) {
                        typeList.add(Class.forName("java.lang." + args[i]));
                    }
                    tableProvider.createTable(name, typeList);
                    serverOut.writeInt(0);
                    assertNotNull(tableProvider.getTable("First")); //Checking that tables "First" exists
                    String showCommand = serverIn.readUTF();
                    assertEquals(showCommand, "show");
                    List<String> tableNames = tableProvider.getTableNames();
                    serverOut.writeInt(tableNames.size());
                    for (String it: tableNames) {
                        serverOut.writeUTF(it);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            client = new Thread(() -> {
                Class[] typeList = {Integer.class, String.class};
                try {
                    remoteTableProvider.createTable("First", Arrays.asList(typeList));
                    List<String> tableNames = remoteTableProvider.getTableNames();
                    assertEquals(tableNames.size(), 1); //checking that there is only one table
                    assertEquals(tableNames.get(0), "First"); //checking that it was our table "First"
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            client.start();
            server.start();
            client.join();
            server.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
    }
}
