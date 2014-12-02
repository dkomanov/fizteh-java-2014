package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ShowTables {
    Integer dirAndFileNum = 16;
    void showTablesFunction(Map<String, String> storage, String currTableName) throws IOException {
        String tableName = new String();
        int recordsAmount = 0;
        File file = new File(System.getProperty("fizteh.db.dir"));
        for (File sub : file.listFiles()) {
            recordsAmount = 0;
            for (Integer i = 0; i < dirAndFileNum; ++i) {
                tableName = System.getProperty("fizteh.db.dir") + "/" + sub.getName() + "/"
                        + i.toString() + ".dir";
                File file1 = new File(tableName);
                if (file1.exists()) {
                    for (Integer j = 0; j < dirAndFileNum; ++j) {
                        tableName = System.getProperty("fizteh.db.dir") + "/" + sub.getName() + "/"
                                + i.toString() + ".dir" + "/" + j.toString() + ".dat";
                        file1 = new File(tableName);
                        if (file1.exists()) {
                            DataInputStream stream = new DataInputStream(new FileInputStream(tableName));
                            byte[] data = new byte[(int) file1.length()];
                            stream.read(data);
                            String temp = new String(data, StandardCharsets.UTF_8);
                            recordsAmount += (temp.length() - temp.replaceAll(" ", "").length()) / 4;
                        }
                    }
                }
            }
            tableName = new File(new File(new File(tableName).getParent()).getParent()).getName();
            if (sub.getName().equals(currTableName)) {
                System.out.println(sub.getName() + " " + (recordsAmount + storage.size()));
            } else {
                System.out.println(sub.getName() + " " + recordsAmount);
            }
        }
    }
}
