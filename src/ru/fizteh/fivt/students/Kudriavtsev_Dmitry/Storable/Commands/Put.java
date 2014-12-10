package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.CurrentStoreable;

import java.io.File;
import java.text.ParseException;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends StoreableCommand {

    public Put() {
        super("put", 2);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (args == null || args.length <= 1) {
            return !batchModeInInteractive;
        }
        if (args.length > 2) {
            for (int i = 2; i < args.length; ++i) {
                args[1] = args[1].concat(" " + args[i]);
            }
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        /*if (dbConnector.activeTable.signature.size() != 2) {
            System.err.println("Bad number of arguments");
            return !batchModeInInteractive;
        }
        ArrayList<Class<?>> newArgs = new ArrayList<>();
        for (int i = 1; i < args.length; ++i) {
            newArgs.add( ((dbConnector.activeTable.signature.get(i - 1)) args[i]) );
            if (!dbConnector.activeTable.signature.contains(args[i].getClass())) {
                System.err.println("Signature not contains class " + args[i].getClass());
                return !batchModeInInteractive;
            }
        }
        Storeable temp = new CurrentStoreable(dbConnector.activeTable.signature);
        for (int i = 1; i < args.length; ++i) {
            temp.setColumnAt(i - 1, args[i]);
        }*/
        Storeable value;
        try {
            value = dbConnector.getActiveTable().put(args[0],
                    dbConnector.getActiveTableProvider().deserialize(dbConnector.getActiveTable(), args[1]));
        } catch(ParseException e) {
            System.err.println("Parse Exception in deserialize");
            return !batchModeInInteractive;
        }
        if (value != null) {
            System.out.println("overwrite");
            for (Object val: ((CurrentStoreable) value).getValues()) {
                System.out.println(val.toString());
            }
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.getActiveTable().whereToSave("", args[0]).getKey();
        if (new File(newPath).exists() || dbConnector.getActiveTable().getChangedFiles().containsKey(newPath)) {
            Integer collisionCount = dbConnector.getActiveTable().getChangedFiles().get(newPath);
            if (collisionCount == null) {
                collisionCount = dbConnector.getActiveTable().countOfCollisionsInFile(new File(newPath).toPath());
            }
            ++collisionCount;
            dbConnector.getActiveTable().getChangedFiles().put(newPath, collisionCount);
        } else {
            dbConnector.getActiveTable().getChangedFiles().put(newPath, 0);
        }
        return true;
    }
}
