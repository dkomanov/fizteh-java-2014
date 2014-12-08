package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ServerMain;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 06.12.2014.
 */
public class ClientThread implements Runnable {
    private ServerSocket serverSocket;
    private TableProvider tableProvider;
    private List<Class<?>> currentTypeList = new ArrayList<>();
    private volatile String clientInformation;
    private Object isClientConnected;
    private volatile boolean isClosed = false;

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
            if (ServerMain.isServerClosed()) {
                synchronized (isClientConnected) {
                    isClientConnected.notifyAll();
                }
            }
            synchronized (isClientConnected) {
                isClientConnected.notifyAll();
            }
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            boolean endOfCycle = false;
            while (!endOfCycle) {
                String firstCommand = in.readUTF();
                String[] args = firstCommand.split("\\s+");
                switch (args[0]) {
                    case "get":
                        getCommand(out, args);
                        break;
                    case "create":
                        createCommand(out, args);
                        break;
                    case "drop":
                        removeCommand(out, args);
                        break;
                    case "close":
                        closeCommand();
                        break;
                    case "put":
                        tableCommand(args);
                        break;
                    case "remove":
                        tableCommand(args);
                        break;
                    case "commit":
                        tableCommand(args);
                        break;
                    case "rollback":
                        tableCommand(args);
                        break;
                    case "disconnect":
                        endOfCycle = true;
                        break;
                    case "set":
                        setCommand(args);
                        break;
                    case "alive":
                        isAlive(out);
                        break;
                    default:
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("socket closed")) {
                System.exit(0);
            } else {
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
            out.writeUTF("" + keys.size());
            StringBuilder typesBuilder = new StringBuilder();
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                typesBuilder.append(table.getColumnType(i).getSimpleName());
                if (i != table.getColumnsCount() - 1) {
                    typesBuilder.append(" ");
                }
            }
            out.writeUTF(new String(typesBuilder));
            for (String key: keys) {
                out.writeUTF(key + " " + new JSONArray(table.get(key)).toString());
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

    public void removeCommand(DataOutputStream out, String[] args) throws IOException {
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

    public void closeCommand() {
        try {
            ((MyTableProvider) tableProvider).close();
        } catch (Exception e) {
            System.out.println("Failed to close");
        }
    }

    public void tableCommand(String[] args) throws IOException {
        ((MyTableProvider) tableProvider).handleTable(args);
    }

    public void setCommand(String[] args) throws IOException {
        ((MyTableProvider) tableProvider).setWorkingTable(args[1]);
    }

    public String getClientInformation() {
        return clientInformation;
    }

    public void isAlive(DataOutputStream out) {
        try {
            if (isClosed) {
                out.writeUTF("no");
            } else {
                out.writeUTF("yes");
            }
        } catch (IOException e) {
            System.out.println("isAlive: failed to send data");
        }
    }

    public void closeConnection() {
        isClosed = true;
    }
}
