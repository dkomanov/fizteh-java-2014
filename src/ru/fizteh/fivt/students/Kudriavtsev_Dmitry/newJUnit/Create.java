package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Create extends JUnitCommand {

    public Create() {
        super("create", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        dbConnector.activeTableProvider.createTable(args[0]);
        return true;
    }
}
