package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTable;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Kirill on 19.10.2014.
 */
public class Status {
    Object object;
    private RemoteTableProvider tableProvider;

    public Status(Object object) {
        this.object = object;
    }

    public FileMap getFileMap() {
        return object instanceof FileMap ? (FileMap) object : null;
    }

    public MyTableProvider getMultiMap() {
        return object instanceof MyTableProvider ? (MyTableProvider) object : null;
    }

    public MyTable getMultiTable() {
        return object instanceof MyTable ? (MyTable) object : null;
    }

    public ServerSocket getServerSocket() {
        return object instanceof ServerSocket ? (ServerSocket) object : null;
    }

    public void setServerSocket(ServerSocket ss) {
        object = ss;
    }

    public Socket getSocket() {
        return object instanceof Socket ? (Socket) object : null;
    }

    public void setSocket(Socket socket) {
        object = socket;
    }

    public RemoteTableProvider getTableProvider() {
        return tableProvider;
    }

    public void setTableProvider(RemoteTableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }
}
