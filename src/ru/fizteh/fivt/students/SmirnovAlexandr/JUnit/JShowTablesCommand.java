package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.ShowTables;
public class JShowTablesCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        Command show = new ShowTables();
        show.execute(base.getUsual());
    }

    protected final int arg = 0;

    protected int getArg() {
        return arg;
    }
}
