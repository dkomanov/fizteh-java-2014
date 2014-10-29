package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Table extends HashMap<String, String> {
    private Path tablePath;
    static final int DIR_COUNT = 16;
    static final int FILE_COUNT = 16;

    Table(Path path) {
        tablePath = path;
    }

    public void readFromTable() throws IOException {
        clear();
        for (int dir = 0; dir < DIR_COUNT; ++dir) {
            for (int file = 0; file < FILE_COUNT; ++file) {
                Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                if (Files.exists(filePath)) {
                    DataInputStream in = new DataInputStream(Files.newInputStream(filePath));
                    while (true) {
                        try {
                            String key = readWord(in);
                            String value = readWord(in);
                            put(key, value);
                        } catch (EOFException e) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private String readWord(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] word = new byte[length];
        in.read(word, 0, length);
        String str = new String(word, "UTF-8");
        return str;
    }

    public void writeToTable() throws IOException {
        Map<String, String>[][] db = new Map[DIR_COUNT][FILE_COUNT];
        for (int i = 0; i < DIR_COUNT; i++) {
            for (int j = 0; j < FILE_COUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, String> entry : entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Integer hashCode = key.hashCode();
            Integer dir = hashCode % DIR_COUNT;
            Integer file = hashCode / DIR_COUNT % FILE_COUNT;
            db[dir][file].put(key, value);
        }
        for (int i = 0; i < DIR_COUNT; i++) {
            for (int j = 0; j < FILE_COUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    Integer nDir = i;
                    Integer nFile = j;
                    Path dirPath = tablePath.resolve(nDir + ".dir");
                    String newPath = dirPath.toString();
                    File directory = new File(newPath);
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            throw new IOException("Cannot create directory");
                        }
                    }
                    String newFilePath = dirPath.resolve(nFile + ".dat").toString();
                    File file = new File(newFilePath);
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new IOException("Cannot create file");
                        }
                    }
                    DataOutputStream out = new DataOutputStream(
                            new FileOutputStream(newFilePath));
                    for (Map.Entry<String, String> entry : db[i][j].entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        writeWord(out, key);
                        writeWord(out, value);
                    }
                    out.close();
                } else {
                    Integer nDir = i;
                    Integer nFile = j;
                    Path dirPath = tablePath.resolve(nDir + ".dir");
                    String newPath = dirPath.toString();
                    File directory = new File(newPath);
                    if (directory.exists()) {
                        String newFilePath = dirPath.resolve(nFile + ".dat").toString();
                        File file = new File(newFilePath);
                        Files.deleteIfExists(file.toPath());
                    }
                }
            }
        }
        File pathDirectory =  tablePath.toFile();
        File[] tableDirectories = pathDirectory.listFiles();
        for (File directory: tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            if (directoryFiles.length == 0) {
                Files.delete(directory.toPath());
            }
        }
    }

    private void writeWord(DataOutputStream out, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        out.writeInt(byteWord.length);
        out.write(byteWord);
        out.flush();
    }
}
