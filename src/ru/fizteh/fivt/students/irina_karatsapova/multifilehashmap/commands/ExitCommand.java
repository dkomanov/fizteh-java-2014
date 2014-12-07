package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;

public class ExitCommand implements Command {
    public void execute(String[] args) throws Exception {
        SaveTable.start();
        System.exit(0);
    }
    
    public String name() {
        return "exit";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
