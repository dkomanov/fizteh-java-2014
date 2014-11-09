package ru.fizteh.fivt.students.pershik.MultiFileHashMap;

import ru.fizteh.fivt.students.pershik.FileMap.FileMap;
import ru.fizteh.fivt.students.pershik.FileMap.InvalidCommandException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pershik on 10/19/14.
 */
public class Table extends FileMap {
    public Table(String newDbDir) {
        dbDirPath = newDbDir;
        db = new HashMap<>();
        readDb();
    }

    private final int mod = 16;
    protected String dbDirPath;

    @Override
    protected void readDb() {
        File dbDir = new File(dbDirPath);
        String[] subdirectories = dbDir.list();
        for (String subdirectory : subdirectories) {
            String dirPath = dbDirPath + File.separator + subdirectory;
            File dir = new File(dirPath);
            if (!dir.isDirectory() || !isCorrectName(subdirectory, ".dir")
                    || !isCorrectSubdirectory(dir)) {
                System.err.println("Incorrect database directory");
                System.exit(-1);
            }
            String[] files = dir.list();
            for (String file : files) {
                String filePath = dirPath + File.separator + file;
                readFile(new File(filePath));
            }
        }
    }

    private void readFile(File file) {
        try {
            try (DataInputStream stream = new DataInputStream(
                    new FileInputStream(file))) {
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

    private boolean isCorrectSubdirectory(File dir) {
        String[] files = dir.list();
        for (String file : files) {
            File curFile = new File(file);
            if (curFile.isDirectory() || !isCorrectName(file, ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectName(String name, String suf) {
        try {
            if (name.length() < 4) {
                return false;
            }
            if (!name.endsWith(suf)) {
                return false;
            }
            name = name.replace(suf, "");
            int num = Integer.parseInt(name);
            return (0 <= num && num <= 15);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void writeDb() {
        Map<String, String>[][] dbParts = new HashMap[mod][mod];
        for (int i = 0; i < mod; i++) {
            for (int j = 0; j < mod; j++) {
                dbParts[i][j] = new HashMap<>();
            }
        }
        for (String key : db.keySet()) {
            int hashCode = key.hashCode();
            int dirNumber = hashCode % mod;
            if (dirNumber < 0) {
                dirNumber += 16;
            }
            int fileNumber = hashCode / mod % mod;
            if (fileNumber < 0) {
                fileNumber += 16;
            }
            dbParts[dirNumber][fileNumber].put(key, db.get(key));
        }
        for (int i = 0; i < mod; i++) {
            String dirPath = dbDirPath + File.separator
                    + Integer.toString(i) + ".dir";
            File dir = new File(dirPath);
            for (int j = 0; j < mod; j++) {
                String path = dirPath + File.separator
                        + Integer.toString(j) + ".dat";
                File file = new File(path);
                writeFile(dbParts[i][j], dir, file);
            }
        }
    }

    protected void writeFile(
            Map<String, String> dbPart, File dir, File file) {
        if (dbPart.isEmpty()) {
            return;
        }
        if (!dir.mkdir()) {
            System.err.println("Can't create directory");
            System.exit(-1);
        }
        try {
            try (DataOutputStream stream = new DataOutputStream(
                    new FileOutputStream(file))) {
                for (String key : dbPart.keySet()) {
                    writeToken(stream, key);
                    writeToken(stream, dbPart.get(key));
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred");
            System.exit(-1);
        }

    }

    protected int executeCommand(String command)
            throws InvalidCommandException {
        String[] tokens = parseCommand(command);
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
            case "":
                break;
            default:
                errorUnknownCommand(tokens[0]);
                break;
        }
        return db.size();
    }
}
