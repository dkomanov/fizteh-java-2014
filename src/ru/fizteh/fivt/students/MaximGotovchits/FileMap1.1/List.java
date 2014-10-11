package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.*;

public class List extends FileMapMain {
    void listFunction() {
        Integer size = 0;
        Set k = storage.keySet();
        for (Object iter : k) {
            if (size < storage.size() - 1) {
                System.out.print(iter + ", ");
            } else if (storage.size() != 1) {
                System.out.print(iter);
            }
            ++size;
        }
        System.out.println();
    }
}

