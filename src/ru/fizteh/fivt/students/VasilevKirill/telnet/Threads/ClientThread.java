package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

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

    public ClientThread(ServerSocket serverSocket, TableProvider tableProvider) {
        this.serverSocket = serverSocket;
        this.tableProvider = tableProvider;
    }

    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String firstCommand = in.readUTF();
            String[] args = firstCommand.split("\\s+");
            switch (args[0]) {
                case "get":
                    getCommand(out, args);
                    break;
                case "create":
                    createCommand(out, args);
                    break;
                default:
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
                throw new IOException("Incorrect arguments");
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
}
