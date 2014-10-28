package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.util.AbstractQueue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author AlexeyZhuravlev
 */
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
            for (String s: scanner.nextLine().split(";\\s*")) {
                activeCommands.add(s);
            }
        }
        return activeCommands.remove();
    }
}
