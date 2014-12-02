package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.util.*;

class ModeWork {
    private Map<String, String> map;

    public ModeWork(Map<String, String> time) {
        map = time;
    }

    int stepMode(String[] array, Functional f, int index) {
        switch (array[0 + index]) {
        case "put":
            f.put(array[1 + index], array[2 + index]);
            return 2;
        case "get":
            f.get(array[1 + index]);
            return 1;
        case "remove":
            f.remove(array[1 + index]);
            return 1;
        case "list":
            f.list();
            return 1;
        case "exit":
            return -1;
        default:
            return -1;
        }
    }

    void usermode() {
        Functional func = new Functional(map);
        String str = new String();
        String[] array;
        Scanner rd = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            str = rd.nextLine().toString();
            array = str.trim().split(" ");
            if (stepMode(array, func, 0) == -1) {
                break;
            }
        }

    }

    void interactive(String[] mass) {
        int offset = 0;
        Functional func = new Functional(map);
        int i = 0;
        while (true) {
            if (i < mass.length) {
                offset = stepMode(mass, func, i);
                i += offset;
                if (offset == -1) {
                    break;
                }
                i++;
            } else {
                break;
            }
        }

    }

}
