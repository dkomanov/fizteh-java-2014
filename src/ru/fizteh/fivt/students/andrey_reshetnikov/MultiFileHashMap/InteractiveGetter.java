package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.AbstractQueue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InteractiveGetter implements CommandGetter {
    Scanner scanner;
    AbstractQueue<String> activeCommands;

    public InteractiveGetter() {
        scanner = new Scanner(System.in);
        activeCommands = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String nextCommand() {
        if (activeCommands.isEmpty()) {
            System.out.print("$ ");
            System.out.flush();
            for (String s: scanner.nextLine().split(";")) {
                activeCommands.add(s.replaceFirst(" *", ""));
            }
        }
        return activeCommands.remove();
    }
}
