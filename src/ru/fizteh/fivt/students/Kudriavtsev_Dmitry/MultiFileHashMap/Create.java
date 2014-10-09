package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Create extends Command {

    public Create() {
        super("create", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        MFHMap map = new MFHMap(dbConnector.dbRoot.resolve(args[0]));
        dbConnector.tables.put(args[0], map);
        System.out.println("created");
        return true;
    }
}
