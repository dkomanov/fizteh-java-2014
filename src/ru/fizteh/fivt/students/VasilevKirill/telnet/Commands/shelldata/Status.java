package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTable;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.net.ServerSocket;

/**
 * Created by Kirill on 19.10.2014.
 */
public class Status {
    Object object;

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
}
