package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

/**
 * Created by Maxim on 07.10.2014.
 */
public class Remove extends FileMapMain {
    void removeFunction() {
        if (cmdBuffer.length == 2) {
            String v = storage.remove(cmdBuffer[1]);
            if (v != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.err.println("incorrect syntax");
        }
    }
}

