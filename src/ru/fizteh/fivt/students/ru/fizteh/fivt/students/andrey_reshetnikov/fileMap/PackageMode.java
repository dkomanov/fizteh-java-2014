package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Hoderu on 09.10.14.
 */
public class PackageMode implements CommandProcess {

    public String[] commands;
    public PackageMode(String[] args) {
        StringBuilder s1 = new StringBuilder();
        for (String s : args) {
            s1.append(s);
            s1.append(' ');
        }
        commands = s1.toString().split(";");
    }

    @Override
    public void process(CommandFromString commandFromString) throws UnknownCommand {
        for (String s : commands) {
            AtomicBoolean flag = new AtomicBoolean(true);
            try {
                commandFromString.fromString(s).execute();
            } catch (StopProcess e) {
                flag.set(false);
            }
            if (!flag.get()) {
                break;
            }
        }
    }
}
