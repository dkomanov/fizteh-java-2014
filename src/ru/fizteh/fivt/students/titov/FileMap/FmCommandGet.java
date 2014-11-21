package ru.fizteh.fivt.students.titov.FileMap;

public class FmCommandGet extends CommandFileMap {
    public FmCommandGet() {
        name = "get";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        String value = myFileMap.get(args[1]);
        if (value != null) {
            System.out.println("found\n" + value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
