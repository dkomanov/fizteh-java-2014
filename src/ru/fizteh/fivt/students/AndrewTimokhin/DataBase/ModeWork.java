package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.util.*;

class ModeWork {
    private Map<String, String> map;

    public ModeWork(Map<String, String> time) {
        map = time;
    }

    int stepmode(String[] array, Functional f, int index) {
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
        String[] commandAgregat;
        Scanner rd = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            str = rd.nextLine().toString();
            commandAgregat = str.trim().split(" ");
            if (stepmode(commandAgregat, func, 0) == -1) {
                break;
            }
        }

    }

    void interactive(String[] mass) {
        Functional func = new Functional(map);
        String str = new String();
        String[] filtr = mass[0].trim().split("\\s+");
        int i = 0;
        while (true) {
            str = filtr[i].toString();
            if (stepmode(filtr, func, i) == -1) {
                break;
            }
            i++;
        }

    }

}
