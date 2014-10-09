package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Use extends Command {

    public Use() {
        super("use", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.out.println(args[0] + " not exists");
            return true;
        }
        if (dbConnector != null) {
            dbConnector.activeTable.unload();
        }
        dbConnector.activeTable = map;
        System.out.println("using " + args[0]);
        return true;
    }

}
