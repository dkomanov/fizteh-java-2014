package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FillingDB {
    public void fillingDBFunction(Map<String, String> storage) throws Exception {
        File file = new File(System.getProperty("db.file"));
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(System.getProperty("db.file"), true));
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
        for (Map.Entry<String, String> entry : storage.entrySet()) {
            byte[] bytesKey = (" " + entry.getKey() + " ").getBytes(StandardCharsets.UTF_8);
            stream.write((int) bytesKey.length);
            stream.write(bytesKey);
            byte[] bytesVal = ((" " + entry.getValue() + " ").getBytes(StandardCharsets.UTF_8));
            stream.write((Integer) bytesVal.length);
            stream.write(bytesVal);
        }
        stream.close();
    }
}


