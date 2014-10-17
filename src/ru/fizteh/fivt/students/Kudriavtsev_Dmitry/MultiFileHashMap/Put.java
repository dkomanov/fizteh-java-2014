package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends Command {

    public Put() {
        super("put", 2);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        if (dbConnector.activeTable == null) {
            System.err.println("No table are used now");
            return false;
        }
        String value = dbConnector.activeTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("overwrite\n" + value);
            //dbConnector.activeTable.changedFiles.add(value);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
