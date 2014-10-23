package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

import java.util.Map;

public class ShowTables extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (args.length > 2) {
            table.manyArgs("show tables");
        }
        if (args.length < 2) {
            table.missingOperand("show tables");
        }
        table.showTables(args);
    }
}
