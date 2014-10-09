package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class InteractiveMode implements CommandProcess{

    @Override
    public void process(CommandFromString commandFromString) throws UnknownCommand {
        Scanner sc = new Scanner(System.in);
        AtomicBoolean flag = new AtomicBoolean(false);
        do {
            System.out.print("$ ");
            for (String s : sc.nextLine().split(";")) {
                try {
                    commandFromString.fromString(s).execute();
                } catch (StopProcess e) {
                    flag.set(true);
                }
                if (flag.get()) {
                    break;
                }
            }
        } while (!flag.get());
    }
}
