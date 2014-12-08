package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class RollbackJUnitCommand implements Commands{
    public void execute(String[] args, FileMap fileMap) throws MyException {

        if (args.length == 1) {
            if (fileMap != null) {
                MultiFileHashMapMain.currentTable.clear();
                MultiFileHashMapMain.currentTable.putAll(MultiFileHashMapMain.committedTable);
                System.out.println(fileMap.changedKeys.size());
                fileMap.changedKeys.clear();
            } else {
                throw new MyException("no table");
            }
        } else {
            throw new MyException("rollback: too many arguments");
        }

    }

    public String getName() {
        return "rollback";
    }
}
