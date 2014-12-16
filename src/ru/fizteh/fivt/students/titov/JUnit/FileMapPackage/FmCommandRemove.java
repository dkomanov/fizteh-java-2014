package ru.fizteh.fivt.students.titov.JUnit.FileMapPackage;

public class FmCommandRemove extends CommandFileMap {
    public FmCommandRemove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        String value = myFileMap.remove(args[1]);
        if (value != null) {
            System.err.println("removed");
        } else {
            System.err.println("not found");
        }
        return true;
    }
}

