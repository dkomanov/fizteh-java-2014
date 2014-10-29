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
        if (!checkArguments(args.length)) {
            if (packageModeInInteractive) {
                return false;
            }
            return true;
        }
        MFHMap map = new MFHMap(dbConnector.dbRoot.resolve(args[0]));
        if (dbConnector.tables.containsKey(args[0])) {
            System.out.println(args[0] + " exists");
        } else {
            dbConnector.tables.put(args[0], map);
            System.out.println("created");
        }
        return true;
    }
}
