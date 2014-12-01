package ru.fizteh.fivt.students.alexpodkin.FileMap;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Reader {

    private String fileMapPath;

    public Reader(String path) {
        fileMapPath = path;
    }

    private String readWord(DataInputStream dataInputStream) throws IOException {
        int length = dataInputStream.readInt();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(dataInputStream.readChar());
        }
        return stringBuilder.toString();
    }

    public HashMap<String, String> readDataFromFile() throws IOException {
        HashMap<String, String> resultFileMap = new HashMap<>();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(fileMapPath));
        while (true) {
            try {
                String key = readWord(dataInputStream);
                String value = readWord(dataInputStream);
                resultFileMap.put(key, value);
            } catch (EOFException e) {
                break;
            }
        }
        return resultFileMap;
    }

}
