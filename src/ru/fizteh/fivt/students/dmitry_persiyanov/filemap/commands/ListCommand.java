package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ListCommand extends Command {
    public ListCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 1) {
            throw new WrongSyntaxException("list");
        }
        Set<String> keySet = hashMap.keySet();
        Iterator<String> keySetIter = keySet.iterator();
        List<String> keysList = new LinkedList<>();
        while (keySetIter.hasNext()) {
            keysList.add(keySetIter.next());
        }
        msg = String.join(", ", keysList);
    }
}
