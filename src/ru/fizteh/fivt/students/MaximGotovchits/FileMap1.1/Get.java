package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

public class Get extends FileMapMain {
    protected void getFunction() {
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
            System.err.println("incorrect syntax");
        }
    }
}

