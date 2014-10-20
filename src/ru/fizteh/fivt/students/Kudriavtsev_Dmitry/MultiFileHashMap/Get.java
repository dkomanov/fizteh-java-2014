package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


/**
 * Created by Дмитрий on 04.10.14.
 */
public class Get extends Command {

    public Get() {
        super("get", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        if (dbConnector.activeTable == null) {
            noTable();
            return false;
        }
        String value = dbConnector.activeTable.get(args[0]);
        if (value != null) {
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
