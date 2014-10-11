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
        StringBuilder keysList = new StringBuilder();
        while (keySetIter.hasNext()) {
            String key = keySetIter.next();
            if (keySetIter.hasNext()) {
                keysList.append(key + ", ");
            } else {
                keysList.append(key);
            }
        }
        msg = new String(keysList.toString());
    }
}
