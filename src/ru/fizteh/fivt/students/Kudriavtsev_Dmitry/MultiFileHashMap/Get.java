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
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        if (dbConnector.activeTable == null) {
            System.err.println("No table are used now");
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
