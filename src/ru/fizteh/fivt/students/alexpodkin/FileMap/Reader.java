package ru.fizteh.fivt.students.alexpodkin.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Reader {

    private String fileMapPath;

    public Reader(String path) {
        fileMapPath = path;
    }

    private String readWord(DataInputStream dataInputStream) throws IOException {
        byte[] bytes = new byte[4];
        bytes[0] = dataInputStream.readByte();
        try {
            dataInputStream.readFully(bytes, 1, 3);
        } catch (EOFException e) {
            throw new IOException("Something goes wrong");
        }
        int length = ByteBuffer.wrap(bytes).getInt();
        if (length < 0) {
            throw new IOException("Something goes wrong");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < length; i++) {
            try {
                byteArrayOutputStream.write(dataInputStream.readByte());
            } catch (EOFException e) {
                throw new IOException("Something goes wrong");
            }
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
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
