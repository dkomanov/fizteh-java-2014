package ru.fizteh.fivt.students.titov.FileMap;

public class FmCommandRemove extends CommandFileMap {
    public FmCommandRemove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        String value = myFileMap.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
