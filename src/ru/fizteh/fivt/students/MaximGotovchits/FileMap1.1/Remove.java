package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.Map;

public class Remove extends Commands {
    void removeFunction(Map<String, String> storage, String[] cmdBuffer) {
        if (cmdBuffer.length == 2) {
            String v = storage.remove(cmdBuffer[1]);
            if (v != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            errorFunction();
        }
    }
}

