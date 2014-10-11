package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

public class Put extends FileMapMain {
    void putFunction() {
        if (cmdBuffer.length == 3) {
            String k = cmdBuffer[1];
            String v = cmdBuffer[2];
            String prevV = storage.put(k, v);
            if (prevV == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(prevV);
            }
        }  else {
            System.err.println("incorrect syntax");
        }
    }
}

