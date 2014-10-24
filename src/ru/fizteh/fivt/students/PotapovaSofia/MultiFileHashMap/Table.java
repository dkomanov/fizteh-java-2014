package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Table extends HashMap<String, String> {
    private Path tablePath;

    Table(Path path) {
        tablePath = path;
    }
    public void readToTable() throws IOException {
        clear();
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
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

    public void writeFromTable() throws IOException {
        Files.createDirectory(tablePath);
        for (Map.Entry<String, String> entry : entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            Path dirPath = tablePath.resolve(dir + ".dir");
            Path filePath = dirPath.resolve(file + ".dat");
            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectory(dirPath);
                } catch (IOException ex) {
                    throw new IOException(dirPath + ": unable to create");
                }
            }
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    throw new IOException(filePath + ": unable to create");
                }
            }
            DataOutputStream out = new DataOutputStream(Files.newOutputStream(filePath));
            writeWord(out, key);
            writeWord(out, value);
        }
    }

    private void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        dataOutputStream.writeInt(byteWord.length);
        dataOutputStream.write(byteWord);
    }
}
