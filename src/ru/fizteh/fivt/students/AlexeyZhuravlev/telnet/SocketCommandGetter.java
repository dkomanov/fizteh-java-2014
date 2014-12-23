package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.CommandGetter;

import java.io.InputStream;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author AlexeyZhuravlev
 */
public class SocketCommandGetter implements CommandGetter {
    Scanner scanner;
    AbstractQueue<String> activeCommands;

    public SocketCommandGetter(InputStream stream) {
        scanner = new Scanner(stream);
        activeCommands = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String nextCommand() {
        if (activeCommands.isEmpty()) {
            Collections.addAll(activeCommands, scanner.nextLine().split(";\\s*"));
        }
        return activeCommands.remove();
    }
}
