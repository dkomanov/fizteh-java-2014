package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class FmCommandRollback extends CommandFileMap {
    public FmCommandRollback() {
        name = "rollback";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        System.out.println(myFileMap.rollback());
        return true;
    }
}

