package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

import java.util.Iterator;
import java.util.Set;

public class ListCommand extends Command {
    public ListCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 1) {
            throw new IllegalArgumentException("list: wrong syntax (arguments are prohibited)");
        }
        Set<String> keySet = hashMap.keySet();
        Iterator<String> keySetIter = keySet.iterator();
        while (keySetIter.hasNext()) {
            String key = keySetIter.next();
            if (keySetIter.hasNext()) {
                System.out.print(key + ", ");
            } else {
                System.out.println(key);
            }
        }
    }
}
