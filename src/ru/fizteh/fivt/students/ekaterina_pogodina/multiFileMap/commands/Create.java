package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Create extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        table.create(args[1]);
    }
}
