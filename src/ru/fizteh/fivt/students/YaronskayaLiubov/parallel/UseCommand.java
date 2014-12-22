package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class UseCommand extends Command {
    UseCommand() {
        name = "use";
        numberOfArguments = 2;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) throws MultiFileMapRunTimeException {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }

        String tableName = args[1];
        StoreableDataTable table = (StoreableDataTable) multiFileHashMap.provider.getTable(tableName);
        if (table == null) {
            System.out.println(tableName + " not exists");
        } else {
            //System.out.println("created column type " + table.getColumnType(0).toString());
            if (multiFileHashMap.currTable != null) {
                int unsavedChanges = multiFileHashMap.currTable.unsavedChangesCount();
                if (unsavedChanges > 0) {
                    throw new MultiFileMapRunTimeException(unsavedChanges + " unsaved changes");
                }
            }
            multiFileHashMap.currTable = table;
            System.out.println("using " + tableName);
        }
        return true;
    }
}
