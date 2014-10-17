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
        if (!checkArguments(args.length)) {
            return false;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.out.println(args[0] + " not exists");
            return true;
        }
        if (dbConnector.activeTable != null) {
            dbConnector.activeTable.unload();
        }
        dbConnector.activeTable = map;
        System.out.println("using " + args[0]);
        return true;
    }

}
