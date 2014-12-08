package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Мирон on 08.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class CRemoteTable implements Table {

    private Client client;
    private Parser parser;
    private Table signature;
    private CTableProvider provider;
    private TemporaryFolder folder;

    public CRemoteTable(String host, int port, String name) throws IOException {
        folder = new TemporaryFolder();
        folder.create();
        provider = new CTableProvider(folder.newFolder("TempProviderFolder").toPath());
        parser = new Parser();
        client = new Client();
        client.connect(host, port);
        client.send("use " + name);
        client.read();
        client.send("get columns count");
        int nColumns = Integer.valueOf(client.read());
        ArrayList<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < nColumns; i++) {
            client.send("get column type " + i);
            String type = client.read();
            try {
                types.add(Class.forName(type));
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getMessage());
            }
        }
        signature = provider.createTable("TMPTable", types);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        String res = null;
        try {
            client.send("put " + key + " " + value);
            res = client.read();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        try {
            return parser.deserialize(signature, res);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    @Override
    public Storeable remove(String key) {
        try {
            client.send("remove " + key);
            String res = client.read();
            return parser.deserialize(signature, res);
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }


    @Override
    public int size() {
        try {
            client.send("size");
            String res = client.read();
            return Integer.valueOf(res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public int commit() throws IOException {
        try {
            client.send("commit");
            String res = client.read();
            return Integer.valueOf(res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public int rollback() {
        try {
            client.send("rollback");
            String res = client.read();
            return Integer.valueOf(res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public int getColumnsCount() {
        try {
            client.send("get columns count");
            String res = client.read();
            return Integer.valueOf(res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return 0;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        try {
            client.send("rollback");
            String res = client.read();
            return Class.forName(res);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }
}
