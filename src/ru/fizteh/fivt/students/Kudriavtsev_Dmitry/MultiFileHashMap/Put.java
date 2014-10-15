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
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        if (dbConnector.activeTable == null) {
            System.err.println("No table are used now");
            return false;
        }
        String value = dbConnector.activeTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("Overwrite:\n" + value);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
