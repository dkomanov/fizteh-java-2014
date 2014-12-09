package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class CommitJUnitCommand implements Commands {

    public void execute(String[] args, FileMap fileMap) throws MyException {

        if (args.length == 1) {
            if (fileMap != null) {
                fileMap.write();
                System.out.println(fileMap.changedKeys.size());
                fileMap.changedKeys.clear();
                MultiFileHashMapMain.committedTable.clear();
                MultiFileHashMapMain.committedTable.putAll(MultiFileHashMapMain.currentTable);
            } else {
                throw new MyException("no table");
            }
        } else {
            throw new MyException("commit: too many arguments");
        }

    }

    public String getName() {
        return "commit";
    }
}
