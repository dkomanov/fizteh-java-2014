package ru.fizteh.fivt.students.titov.JUnit.FileMapPackage;

public class FmCommandGet extends CommandFileMap {
    public FmCommandGet() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        String value = myFileMap.get(args[1]);
        if (value != null) {
            System.err.println("found");
            System.err.println(value);
        } else {
            System.err.println("not found");
        }
        return true;
    }
}
