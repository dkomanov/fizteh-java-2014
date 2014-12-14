package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ServerMain;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kirill on 06.12.2014.
 */
public class ClientThread implements Runnable {
    private ServerSocket serverSocket;
    private TableProvider tableProvider;
    private List<Class<?>> currentTypeList = new ArrayList<>();
    private volatile String clientInformation;
    private Object isClientConnected;
    private Object isClosedMonitor = new Object();

    public ClientThread(ServerSocket serverSocket, TableProvider tableProvider, Object isClientConnected) {
        this.serverSocket = serverSocket;
        this.tableProvider = tableProvider;
        this.isClientConnected = isClientConnected;
    }

    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            StringBuilder clientInformationBuilder = new StringBuilder();
            clientInformationBuilder.append(socket.getInetAddress().toString() + ":" + socket.getLocalPort());
            clientInformation = new String(clientInformationBuilder).substring(1);
            synchronized (isClientConnected) {
                ServerMain.setClientConnected(true);
                isClientConnected.notifyAll();
            }
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Set<String> tableCommands = fulfilTableCommands();
            Set<String> providerCommands = fulfilProviderCommands();
            boolean endOfCycle = false;
            while (!endOfCycle) {
                String firstCommand = in.readUTF();
                String[] args = firstCommand.split("\\s+");
                if (args[0].equals("disconnect")) {
                    endOfCycle = true;
                }
                if (tableCommands.contains(args[0])) {
                    tableCommand(args);
                }
                if (providerCommands.contains(args[0])) {
                    Class[] paramTypes = {DataOutputStream.class, String[].class};
                    try {
                        Method method = this.getClass().getMethod(args[0] + "Command", paramTypes);
                        method.invoke(this, out, args);
                    } catch (Exception e) {
                        System.out.println("Server couldn't invoke the method with name " + args[0] + "Command");
                    }
                }
            }
        } catch (IOException e) {
            if (!e.getMessage().equals("socket closed")) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void getCommand(DataOutputStream out, String[] args) throws IOException {
        Table table = tableProvider.getTable(args[1]);
        if (table == null) {
            out.writeInt(-1);
            out.writeUTF("not found");
            out.flush();
        } else {
            List<String> keys = table.list();
            out.writeInt(keys.size());
            StringBuilder typesBuilder = new StringBuilder();
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                typesBuilder.append(table.getColumnType(i).getSimpleName());
                if (i != table.getColumnsCount() - 1) {
                    typesBuilder.append(" ");
                }
            }
            out.writeUTF(new String(typesBuilder));
            for (String key: keys) {
                try {
                    out.writeUTF(key + " " + new JSONArray(table.get(key)).toString());
                } catch (JSONException e) {
                    out.writeUTF(key + " " + "null");
                }
            }
            out.flush();
        }
    }

    public void createCommand(DataOutputStream out, String[] args) throws IOException {
        try {
            if (args.length < 3) {
                throw new IOException("Create: Incorrect arguments");
            }
            List<Class<?>> types = new ArrayList<>();
            for (int i = 2; i < args.length; ++i) {
                types.add(Class.forName("java.lang." + args[i]));
            }
            tableProvider.createTable(args[1], types);
            out.writeInt(0);
        } catch (Exception e) {
            out.writeInt(-1);
            out.writeUTF(e.getMessage());
        }
    }

    public void dropCommand(DataOutputStream out, String[] args) throws IOException {
        try {
            if (args.length != 2) {
                throw new IOException("Remove: incorrect arguments");
            }
            tableProvider.removeTable(args[1]);
            out.writeInt(0);
        } catch (Exception e) {
            out.writeInt(-1);
            out.writeUTF(e.getMessage());
        }
    }

    public void closeCommand(DataOutputStream out, String[] args) {
        try {
            synchronized (isClosedMonitor) {
                ServerMain.closeServer();
            }
            ((MyTableProvider) tableProvider).close();
        } catch (Exception e) {
            System.out.println("Failed to close");
        }
    }

    public void tableCommand(String[] args) throws IOException {
        ((MyTableProvider) tableProvider).handleTable(args);
    }

    public void setCommand(DataOutputStream out, String[] args) throws IOException {
        ((MyTableProvider) tableProvider).setWorkingTable(args[1]);
    }

    public String getClientInformation() {
        return clientInformation;
    }

    public void aliveCommand(DataOutputStream out, String[] args) {
        try {
            synchronized (isClosedMonitor) {
                if (ServerMain.isServerClosed()) {
                    out.writeUTF("no");
                } else {
                    out.writeUTF("yes");
                }
            }
        } catch (IOException e) {
            System.out.println("isAlive: failed to send data");
        }
    }

    public void showCommand(DataOutputStream out, String[] args) {
        try {
            List<String> tableNames = tableProvider.getTableNames();
            out.writeInt(tableNames.size());
            for (String it : tableNames) {
                out.writeUTF(it);
            }
        } catch (IOException e) {
            System.out.println("Show tables: failed to send data");
        }
    }

    private Set<String> fulfilTableCommands() {
        Set<String> tableCommands = new HashSet<>();
        tableCommands.add("put");
        tableCommands.add("remove");
        tableCommands.add("rollback");
        tableCommands.add("commit");
        return tableCommands;
    }

    private Set<String> fulfilProviderCommands() {
        Set<String> providerCommands = new HashSet<>();
        providerCommands.add("get");
        providerCommands.add("create");
        providerCommands.add("drop");
        providerCommands.add("close");
        providerCommands.add("set");
        providerCommands.add("alive");
        providerCommands.add("show");
        return providerCommands;
    }
}
