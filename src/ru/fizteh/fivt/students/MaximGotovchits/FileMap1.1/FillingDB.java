
package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by Maxim on 07.10.2014.
 */

public class FillingDB extends FileMapMain {

    public void fillingDBFunction() throws Exception {
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


