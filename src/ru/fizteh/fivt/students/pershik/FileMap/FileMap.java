package ru.fizteh.fivt.students.pershik.FileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pershik on 9/25/14.
 */
public class FileMap extends Runner {
    private Map<String, String> db;
    private String dbFile;

    public FileMap(String newDbFile) {
        dbFile = newDbFile;
        db = new HashMap<>();
        if (new File(dbFile).exists())
            readDb();
    }

    @Override
    protected void execute(String command) {
        String[] tokens = parseCommand(command);
        try {
            switch (tokens[0]) {
                case "put":
                    put(tokens);
                    break;
                case "get":
                    get(tokens);
                    break;
                case "remove":
                    remove(tokens);
                    break;
                case "list":
                    list(tokens);
                    break;
                case "exit":
                    exit(tokens);
                    break;
                case "":
                    break;
                default:
                    errorUnknownCommand(tokens[0]);
                    break;
            }
            writeDb();
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        }
    }

    private String readToken(DataInputStream stream) throws IOException {
        if (stream.available() == 0)
            return null;
        int size = stream.readInt();
        StringBuilder buf = new StringBuilder("");
        for (int i = 0; i < size; i++) {
            char c = stream.readChar();
            buf.append(c);
        }
        return buf.toString();
    }

    private void readDb() {
        try {
            try (DataInputStream stream = new DataInputStream(
                    new FileInputStream(dbFile))) {
                while (true) {
                    String key = readToken(stream);
                    String value = readToken(stream);
                    if (key == null || value == null) {
                        break;
                    }
                    db.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred");
            System.exit(-1);
        }
    }

    private void writeToken(DataOutputStream stream, String str)
            throws IOException {
        stream.writeInt(str.length());
        stream.writeChars(str);
    }

    private void writeDb() {
        try {
            try (DataOutputStream stream = new DataOutputStream(
                    new FileOutputStream(dbFile))) {
                for (String key : db.keySet()) {
                    writeToken(stream, key);
                    writeToken(stream, db.get(key));
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred");
            System.exit(-1);
        }
    }

    public void put(String[] args) throws InvalidCommandException {
        if (!checkArguments(2, 2, args.length - 1))
            errorCntArguments("put");
        String key = args[1];
        String value = args[2];
        if (!db.containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(db.get(key));
        }
        db.put(key, value);
    }

    public void get(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1))
            errorCntArguments("get");
        String key = args[1];
        if (db.containsKey(key)) {
            System.out.println("found");
            System.out.println(db.get(key));
        } else {
            System.out.println("not found");
        }
    }

    public void remove(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1))
            errorCntArguments("remove");
        String key = args[1];
        if (db.containsKey(key)) {
            db.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public void list(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1))
            errorCntArguments("list");
        String keys = db.keySet().toString();
        if (!"[]".equals(keys))
            System.out.println(keys.substring(1, keys.length() - 1));
        else
            System.out.println();
    }

    private void exit(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("exit");
        }
        exited = true;
    }
}
