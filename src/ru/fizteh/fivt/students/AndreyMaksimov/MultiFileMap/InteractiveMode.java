package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.util.Scanner;
import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InteractiveMode implements CommandMode {
    Scanner in;
    AbstractQueue<String> useCommands;

    public InteractiveMode() {
        in = new Scanner(System.in);
        useCommands = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String mainAimOfWork() {
        if (useCommands.isEmpty()) {
            System.out.print("$ ");
            System.out.flush();
            for (String s : in.nextLine().split(";")) {
                useCommands.add(s.replaceFirst(" *", ""));
            }
        }
        return useCommands.remove();
    }
}

