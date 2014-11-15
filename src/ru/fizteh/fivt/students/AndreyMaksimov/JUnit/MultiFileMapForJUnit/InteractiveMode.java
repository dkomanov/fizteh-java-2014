package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.util.AbstractQueue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InteractiveMode implements CommandGetter {
    Scanner scanner;
    AbstractQueue<String> activeCommands;

    public InteractiveMode() {
        scanner = new Scanner(System.in);
        activeCommands = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String nextCommand() {
        if (activeCommands.isEmpty()) {
            System.out.print("$ ");
            System.out.flush();
            for (String needString: scanner.nextLine().split(";\\s*")) {
                activeCommands.add(needString);
            }
        }
        return activeCommands.remove();
    }
}


