package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

public class RealRemoteTableProvider implements RemoteTableProvider {
    private Socket server;
    private boolean connected;
    private Scanner input;
    private PrintStream output;

    public RealRemoteTableProvider(String hostname, int port) {
        try {
            connected = false;
            server = new Socket(InetAddress.getByName(hostname), port);
            input = new Scanner(server.getInputStream());
            output = new PrintStream(server.getOutputStream());
            connected = true;
        } catch (IOException e) {
            //supress tm
        }
    }

    @Override
    public void close() throws IOException {
        try {
            server.close();
            input.close();
            output.close();
            System.out.println("close");
        } catch (IOException e) {
            System.err.println("error while closing socket");
        }
    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!connected) {
            throw new IllegalStateException("not connected");
        }
        String message = "create " + name + " (" + TypesUtils.toFileSignature(columnTypes) + ")";
        output.print(message);
        System.out.println(message);
        System.out.println("answer: " + input.nextLine());
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return null;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }
}
