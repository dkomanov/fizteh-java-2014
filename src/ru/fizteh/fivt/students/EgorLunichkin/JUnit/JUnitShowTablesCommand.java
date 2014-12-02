package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.ShowTablesCommand;

public class JUnitShowTablesCommand implements JUnitCommand {
    public JUnitShowTablesCommand(MyTableProvider mtp) {
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;

    @Override
    public void run() throws JUnitException {
        Command show = new ShowTablesCommand(myTableProvider.multiDataBase);
        try {
            show.run();
        } catch (Exception ex) {
            throw new JUnitException(ex.getMessage());
        }
    }
}
