package ru.fizteh.fivt.students.artem_gritsay.MultiFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class MultiReader {
    private String fileMapPath;

    public MultiReader(String path) {

        fileMapPath = path;
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

    private String readWord(DataInputStream dataInputStream) throws IOException {
        byte[] bytes = new byte[4];
        bytes[0] = dataInputStream.readByte();
        try {
            dataInputStream.readFully(bytes, 1, 3);
        } catch (EOFException e) {
            throw new IOException(e.getMessage());
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
                throw new IOException(e.getMessage());
            }
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }

}
