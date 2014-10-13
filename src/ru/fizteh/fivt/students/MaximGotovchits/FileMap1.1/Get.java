package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.Map;

public class Get extends Commands {
    protected void getFunction(Map<String, String> storage, String[] cmdBuffer) {
        if (cmdBuffer.length == 2) {
            String k = cmdBuffer[1];
            String v = storage.get(k);
            if (v != null) {
                System.out.println("found");
                System.out.println(v);
            } else {
                System.out.println("not found");
            }
        } else {
            errorFunction();
        }
    }
}

