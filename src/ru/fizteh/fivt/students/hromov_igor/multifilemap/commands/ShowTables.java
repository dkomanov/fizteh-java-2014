package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;

import java.util.Map;
import java.util.NoSuchElementException;

public class ShowTables extends ParentCommand {

    private static boolean isShowTable = false;

    public ShowTables(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (isShowTable) {
            for (Map.Entry<String, DBaseTable> entry : state.getBase().entrySet()) {
                String name = entry.getKey();
                int size = entry.getValue().size();
                System.out.println(name + " " + size);
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void putArguments(String[] args) {
        if (args[1].equals("tables")){
            isShowTable = true;
        };
    }

    @Override
    public int requiredArgsNum() {
        return 1;
    }
}
