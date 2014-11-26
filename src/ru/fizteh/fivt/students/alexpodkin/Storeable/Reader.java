package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;

public class Reader {

    private String fileMapPath;
    private StoreableTableProvider storeableTableProvider;
    private Table storeableTable;

    public Reader() {
    }

    public Reader(String path, StoreableTableProvider stp, Table st) {
        fileMapPath = path;
        storeableTableProvider = stp;
        storeableTable = st;
    }

    public String readWord(DataInputStream dataInputStream) throws IOException {
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

    public HashMap<String, Storeable> readDataFromFile() throws IOException {
        HashMap<String, Storeable> resultFileMap = new HashMap<>();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(fileMapPath));
        while (true) {
            try {
                String key = readWord(dataInputStream);
                String value = readWord(dataInputStream);
                Storeable storeable = storeableTableProvider.deserialize(storeableTable, value);
                resultFileMap.put(key, storeable);
            } catch (EOFException | ParseException e) {
                break;
            }
        }
        return resultFileMap;
    }

}
