package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Use extends CommandTools {
    int dirAndFileNum = 16;
    void useFunction(String tableName, String oldTableName) throws Exception {
        if (tableName != oldTableName) {
            String outputName = tableName;
            String tablePath = dataBaseName + "/" + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                if (tableIsChosen) {
                    new FillTable().fillTableFunction(oldTableName);
                    storage.clear();
                    commitStorage.clear();
                }
                for (Integer i = 0; i < dirAndFileNum; ++i) {
                    for (Integer j = 0; j < dirAndFileNum; ++j) {
                        tablePath = dataBaseName + "/" + tableName + "/"
                                + i.toString() + ".dir" + "/" + j.toString() + ".dat";
                        if (new File(tablePath).exists()) {
                            fillStorage(tablePath, file);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
            }
        } else {
            System.out.println("using " + oldTableName);
        }
    }
    void fillStorage(String tableName, File file) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(tableName));
        file = new File(tableName);
        byte[] data = new byte[(int) file.length()];
        stream.read(data);
        int counter = 0;
        int offset = 0;
        String keyForMap = "";
        String valForMap = "";
        while (counter < file.length()) {
            offset = data[counter];
            keyForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            counter = counter + offset + 1;
            offset = data[counter];
            valForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            storage.put(keyForMap, valForMap);
            commitStorage.put(keyForMap, valForMap);
            counter = counter + offset + 1;
        }
        stream.close();
    }
}
